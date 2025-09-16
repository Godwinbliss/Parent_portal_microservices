import { Component, signal, computed, effect } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AuthService } from './services/auth.service';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { ParentDashboardComponent } from './components/parent-dashboard/parent-dashboard.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { CommunicationService } from './services/communication.service';
import { UserService } from './services/user.service';
import { ChatDto, UserDto, MessageDto } from './models/dtos';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    HttpClientModule,
    FormsModule,
    NgIf,
    HomeComponent,
    LoginComponent,
    ParentDashboardComponent,
    AdminDashboardComponent
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'ParentPortalFE';
  showLogin = signal(false);

  // --- Global Chat Management ---
  isChatOpen = signal(false);
  chats = signal<ChatDto[]>([]);
  allUsers = signal<UserDto[]>([]);
  selectedChat = signal<ChatDto | null>(null);
  newMessageContent = signal('');
  chatSearchQuery = signal(''); // NEW: For searching users

  // Updated computed property to filter users based on search
  usersToChatWith = computed(() => {
    const currentUser = this.authService.currentUser();
    if (!currentUser) return [];

    let users: UserDto[] = [];
    if (currentUser.role === 'ADMIN') {
      users = this.allUsers().filter(u => u.role === 'PARENT');
    } else if (currentUser.role === 'PARENT') {
      users = this.allUsers().filter(u => u.role === 'ADMIN');
    }

    const query = this.chatSearchQuery().toLowerCase();
    if (!query) {
      return users;
    }
    return users.filter(u => u.username.toLowerCase().includes(query));
  });

  constructor(
    public authService: AuthService,
    private communicationService: CommunicationService,
    private userService: UserService
  ) {
    effect(() => {
      const user = this.authService.currentUser();
      if (user) {
        this.loadChatData();
      } else {
        this.clearChatData();
      }
    });
  }

  async loadChatData(): Promise<void> {
    const currentUser = this.authService.currentUser();
    if (!currentUser) return;
    try {
      const [users, chats] = await Promise.all([
        this.userService.getUsers(),
        this.communicationService.getChatsByParticipant(currentUser.id)
      ]);
      this.allUsers.set(users || []);
      this.chats.set(chats || []);
    } catch (error) {
      console.error("Failed to load chat data", error);
    }
  }

  clearChatData(): void {
    this.isChatOpen.set(false);
    this.chats.set([]);
    this.allUsers.set([]);
    this.selectedChat.set(null);
  }

  toggleChat(): void {
    this.isChatOpen.set(!this.isChatOpen());
  }

  async selectChatWithUser(user: UserDto): Promise<void> {
    const currentUser = this.authService.currentUser();
    if (!currentUser) return;
    try {
      let chat = this.chats().find(c =>
        (c.participant1Id === user.id && c.participant2Id === currentUser.id) ||
        (c.participant2Id === user.id && c.participant1Id === currentUser.id)
      );

      if (chat) {
        const fullChat = await this.communicationService.getChatMessages(chat.id);
        this.selectedChat.set(fullChat);
      } else {
        const newChat = await this.communicationService.createChat({
          participant1Id: currentUser.id,
          participant2Id: user.id
        });
        this.chats.update(currentChats => [...currentChats, newChat]);
        this.selectedChat.set(newChat);
      }
    } catch (error) {
      console.error("Error selecting or creating chat:", error);
    }
  }

  getChatParticipantName(chat: ChatDto | null): string {
    if (!chat) return '';
    const currentUserId = this.authService.currentUser()?.id;
    const otherParticipantId = chat.participant1Id === currentUserId ? chat.participant2Id : chat.participant1Id;
    const otherUser = this.allUsers().find(u => u.id === otherParticipantId);
    return otherUser?.username || 'User';
  }

  async onSendMessage(): Promise<void> {
    const chatId = this.selectedChat()?.id;
    const content = this.newMessageContent().trim();
    const currentUserId = this.authService.currentUser()?.id;

    if (!chatId || !content || !currentUserId) return;

    try {
      const newMessage = await this.communicationService.addMessageToChat(chatId, { senderId: currentUserId, content });
      this.selectedChat.update(chat => {
        if (chat) {
          newMessage.senderUsername = this.authService.currentUser()?.username || 'Me';
          if (!chat.messages) chat.messages = [];
          chat.messages.push(newMessage);
        }
        return chat ? { ...chat } : null;
      });
      this.newMessageContent.set('');
    } catch (error) {
      console.error('Error sending message:', error);
    }
  }

  navigateToDashboard(): void {
    console.log('Navigating to dashboard...');
  }

  onLogout(): void {
    this.authService.logout();
    this.showLogin.set(false);
  }

  onLoginClick(): void {
    this.showLogin.set(true);
  }
}
