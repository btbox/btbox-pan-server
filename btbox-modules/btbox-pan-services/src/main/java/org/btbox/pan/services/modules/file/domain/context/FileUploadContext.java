package org.btbox.pan.services.modules.file.domain.context;

import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import io.github.linpeilie.annotations.AutoMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.btbox.pan.services.modules.file.domain.entity.PanFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/27 14:43
 * @version: 1.0
 */
@Data
public class FileUploadContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 7775634634622638300L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 文件的总大小
     */
    private Long totalSize;

    /**
     * 文件的父文件夹ID
     */
    private Long parentId;

    /**
     * 文件实体
     */
    private MultipartFile file;

    /**
     * 实体文件记录
     */
    private PanFile record;

}