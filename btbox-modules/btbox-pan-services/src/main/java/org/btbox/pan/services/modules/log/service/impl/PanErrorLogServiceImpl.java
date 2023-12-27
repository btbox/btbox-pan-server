package org.btbox.pan.services.modules.log.service.impl;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.btbox.pan.services.modules.log.domain.entity.PanErrorLog;
import org.btbox.pan.services.modules.log.repository.mapper.PanErrorLogMapper;
import org.btbox.pan.services.modules.log.service.PanErrorLogService;
/**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2023/12/27 17:19
 * @version: 1.0
*/
@Service
public class PanErrorLogServiceImpl extends ServiceImpl<PanErrorLogMapper, PanErrorLog> implements PanErrorLogService{

}
