package com.example.chainsproject.navigation

sealed class NavRoutes(val route: String) {
    // 认证相关
    object Login : NavRoutes("login")
    object Register : NavRoutes("register")
    
    // 主页
    object Home : NavRoutes("home")
    
    // 产品相关
    object ProductList : NavRoutes("product_list")
    object ProductDetail : NavRoutes("product_detail/{productId}") {
        fun createRoute(productId: Long) = "product_detail/$productId"
    }
    object AddProduct : NavRoutes("add_product")
    object EditProduct : NavRoutes("edit_product/{productId}") {
        fun createRoute(productId: Long) = "edit_product/$productId"
    }
    
    // 溯源相关
    object TraceList : NavRoutes("trace_list")
    object TraceDetail : NavRoutes("trace_detail/{traceId}") {
        fun createRoute(traceId: Long) = "trace_detail/$traceId"
    }
    object AddTrace : NavRoutes("add_trace")
    
    // 统计相关
    object Statistics : NavRoutes("statistics")
    
    // 消费者端
    object ConsumerHome : NavRoutes("consumer_home")
    object SupplyHome : NavRoutes("supply_home")
    object ScanQR : NavRoutes("scan_qr")
    
    // 供应链端
    object SupplyChainHome : NavRoutes("supply_chain_home")
    object DataEntry : NavRoutes("data_entry")
    object DataManagement : NavRoutes("data_management")
    
    // 监管端
    object RegulatoryHome : NavRoutes("regulatory_home")
    object DataAudit : NavRoutes("data_audit")
    object IssueTracking : NavRoutes("issue_tracking")
    
    object TraceRecordList : NavRoutes("trace_record_list/{productId}") {
        fun createRoute(productId: Long) = "trace_record_list/$productId"
    }
    object AddTraceRecord : NavRoutes("add_trace_record/{productId}") {
        fun createRoute(productId: Long) = "add_trace_record/$productId"
    }
    
    // 审计相关
    object AuditRecordList : NavRoutes("audit_record_list/{productId}") {
        fun createRoute(productId: Long) = "audit_record_list/$productId"
    }
    object AddAuditRecord : NavRoutes("add_audit_record/{productId}") {
        fun createRoute(productId: Long) = "add_audit_record/$productId"
    }
    
    // 问题相关
    object IssueRecordList : NavRoutes("issue_record_list/{productId}") {
        fun createRoute(productId: Long) = "issue_record_list/$productId"
    }
    object AddIssueRecord : NavRoutes("add_issue_record/{productId}") {
        fun createRoute(productId: Long) = "add_issue_record/$productId"
    }
} 