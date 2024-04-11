package com.ohh.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ohh.linksaucecommon.model.entity.UserInterfaceInfo;
import com.ohh.project.model.vo.InterfaceInfoVO;

import java.util.List;

/**
* @author 12994
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Mapper
* @createDate 2024-04-06 14:43:05
* @Entity generator.domain.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {
    List<InterfaceInfoVO> listTopInvokeInterfaceInfo(int limit);
}




