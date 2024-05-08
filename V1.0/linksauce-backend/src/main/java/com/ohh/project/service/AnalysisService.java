package com.ohh.project.service;

import com.ohh.project.model.vo.InterfaceInfoVO;

import java.util.List;

public interface AnalysisService {
    List<InterfaceInfoVO> listTopInvokeInterfaceInfo(int limit);
}
