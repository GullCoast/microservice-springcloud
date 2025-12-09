package org.example.po;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data  // 自动生成 getter、setter、toString、equals、hashCode
@NoArgsConstructor  // 无参构造函数
@AllArgsConstructor // 全参构造函数
public class Order {
    private String id;
    private Double price;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;

    @Override
    public String toString() {
        return "Order [id=" + id + ", price=" + price + ", "
                + "receiverName=" + receiverName + ", receiverAddress="
                + receiverAddress + ", receiverPhone=" + receiverPhone + "]";
    }
}