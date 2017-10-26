package cn.summerwaves.util;

import java.util.List;

public class DataMigrationUtil {

    public static void AliToQiNiu() {
        List<String> keys = ALiYunUtil.getAllFileName();
        for (String key : keys) {
            QiNiuUtil.upLoad(ALiYunUtil.downLoad(key), key);
        }
    }

    public static void QiNiuToALi() {
        List<String> keys = QiNiuUtil.getAllFileName();
        for (String key : keys) {
            ALiYunUtil.upLoad(QiNiuUtil.downLoad(key), key);
        }
    }
}
