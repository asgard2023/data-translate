package cn.org.opendfl.translate.dflsystem.vo;

import cn.org.opendfl.base.MyPageInfo;
import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class PageResult<T> implements Serializable {
    public PageResult(PageInfo<T> pageInfo) {
        if (pageInfo == null) {
            return;
        }
        this.state=200;
        this.data = pageInfo.getList();
        Long total = pageInfo.getTotal();
        this.total = total.intValue();
        if(pageInfo instanceof MyPageInfo) {
            this.dicts = ((MyPageInfo)pageInfo).getDicts();
        }
    }

    private static final long serialVersionUID = 1L;
    private int state;
    private String msg;
    private Integer total;
    private List<T> data;
    private Map<String, Object> dicts;
}
