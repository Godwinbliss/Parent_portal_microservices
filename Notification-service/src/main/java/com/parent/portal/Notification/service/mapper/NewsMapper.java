package com.parent.portal.Notification.service.mapper;

import com.parent.portal.Notification.service.dto.NewsCreateDto;
import com.parent.portal.Notification.service.dto.NewsDto;
import com.parent.portal.Notification.service.entity.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NewsMapper {

    NewsMapper INSTANCE = Mappers.getMapper(NewsMapper.class);

    @Mapping(target = "authorUsername", ignore = true) // Will be populated by service
    NewsDto newsToNewsDto(News news);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedDate", ignore = true) // Will be set by service
    News newsCreateDtoToNews(NewsCreateDto newsCreateDto);

    List<NewsDto> newsListToNewsDtoList(List<News> newsList);
}
