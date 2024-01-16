package org.btbox.pan.services.modules.share.service.impl;

import lombok.RequiredArgsConstructor;
import org.assertj.core.util.Lists;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.pan.services.modules.share.domain.context.SaveShareFilesContext;
import org.btbox.pan.services.modules.share.domain.entity.PanShare;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import java.rmi.ServerException;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.btbox.pan.services.modules.share.domain.entity.PanShareFile;
import org.btbox.pan.services.modules.share.repository.mapper.PanShareFileMapper;
import org.btbox.pan.services.modules.share.service.PanShareFileService;
/**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2024/1/16 10:38
 * @version: 1.0
*/
@Service
@RequiredArgsConstructor
public class PanShareFileServiceImpl extends ServiceImpl<PanShareFileMapper, PanShareFile> implements PanShareFileService{

    /**
     * 保存分享的文件和对应关系
     *
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2024/1/16 14:34
     * @return: void
     */
    @Override
    public void saveShareFiles(SaveShareFilesContext context) {
        List<Long> shareFileIdList = context.getShareFileIdList();
        Long shareId = context.getShareId();
        Long userId = context.getUserId();

        List<PanShareFile> records = Lists.newArrayList();
        for (Long shareFileId : shareFileIdList) {
            PanShareFile record = new PanShareFile();
            record.setId(IdUtil.get());
            record.setShareId(shareId);
            record.setFileId(shareFileId);
            record.setCreateUser(userId);
            record.setCreateTime(new Date());
            records.add(record);
        }

        if (!this.saveBatch(records)) {
            throw new ServiceException("保存文件分享关联关系失败");
        }

    }
}
