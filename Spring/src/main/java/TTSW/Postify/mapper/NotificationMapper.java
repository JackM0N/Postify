package TTSW.Postify.mapper;

import TTSW.Postify.dto.NotificationDTO;
import TTSW.Postify.model.Notification;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {
    Notification toEntity(NotificationDTO notificationDTO);

    NotificationDTO toDto(Notification notification);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Notification partialUpdate(NotificationDTO notificationDTO, @MappingTarget Notification notification);
}