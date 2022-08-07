package com.meeleet.learn.common.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Select选择器默认Option属性
 *
 * @author jaychang
 */
@Schema(description = "Select选择器默认Option属性")
@Data
@NoArgsConstructor
public class OptionVO<T> implements Serializable {

    private static final long serialVersionUID = -5925150404241675596L;

    public OptionVO(T value, String label) {
        this.value = value;
        this.label = label;
    }

    @Schema(description = "选项的值")
    private T value;

    @Schema(description = "选项的标签，若不设置则默认与value相同")
    private String label;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private List<OptionVO> children;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @Schema(description = "是否禁用该选项，默认false")
    public Boolean disabled;

}