package com.example.web3project.data.repository

import com.example.web3project.data.entity.TraceInfo
import javax.inject.Inject

class TraceRepositoryImpl @Inject constructor() : TraceRepository {
    override suspend fun getTraceInfoByCode(code: String): TraceInfo {
        // TODO: 后续用 Retrofit 调用后端/区块链接口
        return TraceInfo(
            productName = "有机苹果",
            origin = "山东烟台",
            batch = "20230401A",
            processHistory = listOf(
                "采摘：2023-04-01 烟台果园",
                "分拣：2023-04-02 烟台分拣中心",
                "冷链运输：2023-04-03 烟台-北京",
                "入库：2023-04-04 北京仓库"
            ),
            certUrl = "https://example.com/cert/123456"
        )
    }
} 