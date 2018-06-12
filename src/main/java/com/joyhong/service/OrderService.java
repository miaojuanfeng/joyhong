package com.joyhong.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.joyhong.model.Order;

public interface OrderService {
	int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);
    
    int selectCount();
    
    List<Order> selectOffsetAndLimit(Integer offset, Integer limit);
    
    int selectCategoryCount(HttpServletRequest request);
    
    List<Order> selectCategoryOffsetAndLimit(HttpServletRequest request, Integer offset, Integer limit);
    
    int selectSearchCount(String orderCode, String machineCode, String dealerCode, String keyCode);
    
    List<Order> selectSearchOffsetAndLimit(String orderCode, String machineCode, String dealerCode, String keyCode, Integer offset, Integer limit);

    int updateByPrimaryKeySelective(Order record);
    
    int updateByPrimaryKeyWithBLOBs(Order record);

    int updateByPrimaryKey(Order record);
}
