package cn.tsoft.framework.fastdfs.protocol.tracker.response;

import cn.tsoft.framework.fastdfs.mapper.ObjectMateData;
import cn.tsoft.framework.fastdfs.model.StorageState;
import cn.tsoft.framework.fastdfs.protocol.BaseResponse;
import cn.tsoft.framework.fastdfs.utils.FastDfsParamMapperUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取Storage服务器状态响应
 * @author ningyu
 * @date 2017年5月17日 中午12:48 <br/>
 */
public class GetListStorageResponse extends BaseResponse<List<StorageState>> {

    /**
     * 解析反馈内容
     */
    @Override
    public List<StorageState> decodeContent(InputStream in, Charset charset) throws IOException {
        // 解析报文内容
        byte[] bytes = new byte[(int) getContentLength()];
        int contentSize = in.read(bytes);
        if (contentSize != getContentLength()) {
            throw new IOException("读取到的数据长度与协议长度不符");
        }
        return decodeGroup(bytes, charset);
    }

    /**
     * 解析Group
     */
    private List<StorageState> decodeGroup(byte[] bs, Charset charset) throws IOException {
        // 获取对象转换定义
        ObjectMateData objectMateData = FastDfsParamMapperUtils.getObjectMap(StorageState.class);
        int fixFieldsTotalSize = objectMateData.getFieldsFixTotalSize();
        if (bs.length % fixFieldsTotalSize != 0) {
            throw new IOException("FixFieldsTotalSize=" + fixFieldsTotalSize + ", 但是数据长度=" + bs.length + ", 数据无效");
        }
        // 计算反馈对象数量
        int count = bs.length / fixFieldsTotalSize;
        int offset = 0;
        List<StorageState> results = new ArrayList<StorageState>(count);
        for (int i = 0; i < count; i++) {
            byte[] one = new byte[fixFieldsTotalSize];
            System.arraycopy(bs, offset, one, 0, fixFieldsTotalSize);
            results.add(FastDfsParamMapperUtils.map(one, StorageState.class, charset));
            offset += fixFieldsTotalSize;
        }
        return results;
    }
}
