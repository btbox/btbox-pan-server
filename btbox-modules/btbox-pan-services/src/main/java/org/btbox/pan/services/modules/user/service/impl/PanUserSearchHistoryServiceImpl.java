package org.btbox.pan.services.modules.user.service.impl;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.btbox.pan.services.modules.user.domain.entity.PanUserSearchHistory;
import org.btbox.pan.services.modules.user.repository.mapper.PanUserSearchHistoryMapper;
import org.btbox.pan.services.modules.user.service.PanUserSearchHistoryService;
/**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2024/1/3 11:25
 * @version: 1.0
*/
@Service
public class PanUserSearchHistoryServiceImpl extends ServiceImpl<PanUserSearchHistoryMapper, PanUserSearchHistory> implements PanUserSearchHistoryService{

}
