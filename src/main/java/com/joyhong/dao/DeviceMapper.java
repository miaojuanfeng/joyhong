package com.joyhong.dao;

import java.util.List;

import com.joyhong.model.Device;

public interface DeviceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Device record);

    int insertSelective(Device record);

    Device selectByPrimaryKey(Integer id);
    
    Device selectByDeviceToken(String device_token);
    
    List<Device> selectLikeDeviceToken(String device_token);
    
    List<Device> selectByOrderId(Integer order_id);
    
    List<String> selectByOrderIdReturnDeviceToken(Integer order_id);
    
    int selectCountByOrderId(Integer orderId);
    
    int selectCount();
    
    List<Device> selectOffsetAndLimit(Integer offset, Integer limit);
    
    int selectOrderCount(Integer order);
    
    List<Device> selectOrderOffsetAndLimit(Integer order, Integer offset, Integer limit);
    
    int selectSearchCount(String deviceToken, String deviceFcmToken);
    
    List<Device> selectSearchOffsetAndLimit(String deviceToken, String deviceFcmToken, Integer offset, Integer limit);

    int updateByPrimaryKeySelective(Device record);

    int updateByPrimaryKey(Device record);
}