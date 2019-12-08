package com.yoursride.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.yoursride.beans.CustomerBean;
import com.yoursride.beans.DriverBean;

public class DriverAllocation {

	public DriverBean allocateDriver(CustomerBean customer,
			MySQLConnection datasource) {

		PreparedStatement ps = null;
		ResultSet resultSet = null;

		Map<Integer, DriverBean> mapDriver = new HashMap<Integer, DriverBean>();
		Map<Double, Integer> driverCustomerDistance = new TreeMap<Double, Integer>();
		List<Double> list = new LinkedList<Double>();

		Connection con = datasource.getMysqlConnection();
		try {
			ps = con.prepareStatement("SELECT driverName,lat,longitude,id FROM driver WHERE driverStatus=\"AVAILABLE\"");
			resultSet = ps.executeQuery();
			while (resultSet.next()) {

				DriverBean driverBean = new DriverBean();

				driverBean.setName(resultSet.getString(1));
				driverBean.setLat(resultSet.getString(2));
				driverBean.setLongitude(resultSet.getString(3));
				driverBean.setId(resultSet.getInt(4));

				mapDriver.put(resultSet.getInt(4), driverBean);

				Double distance = calculateDistance(customer, driverBean);
				driverCustomerDistance.put(distance, resultSet.getInt(4));
				list.add(distance);
			}
			if (list.size() > 0)
				return mapDriver.get(driverCustomerDistance.get(Collections
						.min(list)));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
				ps.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return null;
	}

	private Double calculateDistance(CustomerBean customer, DriverBean driver) {

		return distance(Double.valueOf(customer.getLat()),
				Double.valueOf(customer.getLongitude()),
				Double.valueOf(driver.getLat()),
				Double.valueOf(driver.getLongitude()));
	}

	private double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344;

		return (dist);
	}

	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}
}
