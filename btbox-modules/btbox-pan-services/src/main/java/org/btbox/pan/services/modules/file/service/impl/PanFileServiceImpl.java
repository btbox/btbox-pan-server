package org.btbox.pan.services.modules.file.service.impl;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.btbox.pan.services.modules.file.repository.mapper.PanFileMapper;
import org.btbox.pan.services.modules.file.domain.entity.PanFile;
import org.btbox.pan.services.modules.file.service.PanFileService;
/**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2023/12/27 10:31
 * @version: 1.0
*/
@Service
public class PanFileServiceImpl extends ServiceImpl<PanFileMapper, PanFile> implements PanFileService{

}
