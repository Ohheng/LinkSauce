package com.ohh.project.controller;

import com.ohh.project.annotation.AuthCheck;
import com.ohh.project.common.BaseResponse;
import com.ohh.project.common.ResultUtils;
import com.ohh.project.model.vo.InterfaceInfoVO;
import com.ohh.project.service.AnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private AnalysisService analysisService;

    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo() {
        List<InterfaceInfoVO> listTopInvokeInterfaceInfo = analysisService.listTopInvokeInterfaceInfo(3);
        return ResultUtils.success(listTopInvokeInterfaceInfo);
    }
}
