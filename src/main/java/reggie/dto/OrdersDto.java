package reggie.dto;

import lombok.Data;
import reggie.entity.OrderDetail;
import reggie.entity.Orders;

import java.util.List;

@Data
public class OrdersDto extends Orders {



    private List<OrderDetail> orderDetails;
	
}
