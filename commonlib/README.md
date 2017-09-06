###公共库说明

CommonLib库属于公共库，主要存放工具类代码。
建议功能性Model都依赖本库。

####使用方法：
在主程序的Application中初始化CommonKit即可。

####功能说明

######com.robot.common.lib.animation包

本包主要存放控件动画

######com.robot.common.lib.constant包

本包主要存放一些常量

######com.robot.common.lib.display包

本包提供屏幕工具：如获取屏幕宽高、密度等

######com.robot.common.lib.file包

本包提供文件存储，复制，获取文件名，获取扩展名等

######com.robot.common.lib.log.crash包

本包提供奔溃日志保存，默认不需要调用本包功能，在CommonKit初始化时即完成本包功能

######com.robot.common.lib.media包

本包提供读取DCIM文件夹下的视频文件

######com.robot.common.lib.net包

本包提供网络探测、网络连接情况等功能

######com.robot.common.lib.permission包

本包提供权限请求等功能，使用例子可查看sample/PermissionSampleActivity

######com.robot.common.lib.share包

本包提供偏好存储功能

######com.robot.common.lib.string包

本包提供字符串处理功能，如加密，转换等

######com.robot.common.lib.sys包

本包提供查询设备系统功能

######声明
如果有修改、增加，请在详细的修改代码库旁注明修改人、时间、原因
