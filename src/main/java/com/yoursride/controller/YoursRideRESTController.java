package com.yoursride.controller;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yoursride.beans.CustomerBean;
import com.yoursride.beans.DriverBean;
import com.yoursride.constants.HandlerConstants;
import com.yoursride.utils.DriverAllocation;
import com.yoursride.utils.MySQLConnection;

@Controller
public class YoursRideRESTController {

	@Autowired
	private MySQLConnection datasource;

	@RequestMapping(value = HandlerConstants.CREATE_CUSTOMER, method = RequestMethod.POST)
	public @ResponseBody String createCustomer(@RequestBody String data) {
		
		Connection con = datasource
				.getMysqlConnection();
		if (data != null && data.length() > 0) {

			String decodedData = null;
			try {
				decodedData = java.net.URLDecoder.decode(data, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			
			Gson gson = new Gson();
			CustomerBean customer = gson.fromJson(decodedData.substring(0, decodedData.length()-1),
					CustomerBean.class);
			
			DriverAllocation driverAllocation = new DriverAllocation();
			DriverBean driverBean = driverAllocation.allocateDriver(customer,datasource);

			if (driverBean != null) {

				PreparedStatement ps = null;
				PreparedStatement ps2 = null;

				Integer customerId = null;

				try {

					// insert new customer only on driver allocation
					ps = con
							.prepareStatement(
									"INSERT INTO customer(orderNumber,customerName,lat,longitude,driverId) values(?,?,?,?,?)",
									Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, "ORDER_" + System.currentTimeMillis());
					ps.setString(2, customer.getName());
					ps.setString(3, customer.getLat());
					ps.setString(4, customer.getLongitude());
					ps.setInt(5, driverBean.getId());
					ps.execute();

					ResultSet generatedKeys = ps.getGeneratedKeys();

					if (generatedKeys.next()) {

						customerId = generatedKeys.getInt(1);
					}

					if (customerId > 0) {

						// change driver status from Available to busy
						ps2 = con
								.prepareStatement(
										"UPDATE driver SET driverStatus=\"BUSY\" WHERE id=?");
						ps2.setInt(1, driverBean.getId());
						ps2.execute();
						return gson.toJson(driverBean);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						ps.close();
						ps2.close();
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		}
		
		return null;
	}

	@RequestMapping(value = HandlerConstants.GET_CUSTOMER, method = RequestMethod.GET)
	public @ResponseBody String getCustomer(@PathVariable("id") String id) {

		PreparedStatement ps = null;
		ResultSet resultSet = null;
		Connection con = datasource
				.getMysqlConnection();
		
		CustomerBean customerBean = new CustomerBean();
		Gson gson = new Gson();

		try {
			ps = con
					.prepareStatement(
							"SELECT id,customerName,lat,longitude FROM customer WHERE id=?");
			ps.setInt(1, Integer.parseInt(id));
			resultSet = ps.executeQuery();
			while (resultSet.next()) {

				customerBean.setId(resultSet.getInt(1));
				customerBean.setName(resultSet.getString(2));
				customerBean.setLat(resultSet.getString(3));
				customerBean.setLongitude(resultSet.getString(4));

			}

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

		return "p(" + gson.toJson(customerBean) + ")";

	}

	@RequestMapping(value = HandlerConstants.GET_ALL_DRIVER_CUSTOMER, method = RequestMethod.GET)
	public @ResponseBody String getAllDriverCustomer() {

		Gson gson = new Gson();
		List<JsonObject> list = new LinkedList<JsonObject>();
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		Connection con = datasource
				.getMysqlConnection();
		
		try {
			ps = con
					.prepareStatement(
							"SELECT d.driverName, c.customerName, d.driverStatus FROM driver d LEFT JOIN customer c ON d.id=c.driverId;");
			resultSet = ps.executeQuery();
			while (resultSet.next()) {

				JsonObject jsonRowObject = new JsonObject();

				String driverName = resultSet.getString(1);
				String customerName = resultSet.getString(2);
				String status = resultSet.getString(3);

				if (customerName == null || customerName.length() <= 0)
					customerName = "-";
				jsonRowObject.addProperty("DriverName", driverName);
				jsonRowObject.addProperty("CustomerName", customerName);
				jsonRowObject.addProperty("Status", status);

				list.add(jsonRowObject);
			}

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

		return "p(" + gson.toJson(list) + ")";
	}

	@RequestMapping(value = HandlerConstants.GET_AlL_DRIVER, method = RequestMethod.GET)
	public @ResponseBody String getAllDriver() {

		PreparedStatement ps = null;
		ResultSet resultSet = null;
		Connection con = datasource
				.getMysqlConnection();
		
		List<DriverBean> list = new LinkedList<>();
		Gson gson = new Gson();

		try {
			ps = con.prepareStatement(
					"SELECT driverName,lat,longitude,id FROM driver");
			resultSet = ps.executeQuery();
			while (resultSet.next()) {

				DriverBean driverBean = new DriverBean();

				driverBean.setName(resultSet.getString(1));
				driverBean.setLat(resultSet.getString(2));
				driverBean.setLongitude(resultSet.getString(3));
				driverBean.setId(resultSet.getInt(4));
				list.add(driverBean);
			}

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

		return "p(" + gson.toJson(list) + ")";
	}

	@RequestMapping(value = HandlerConstants.GET_AlL_AVAILABLE_DRIVER, method = RequestMethod.GET)
	public @ResponseBody String getAllAvailableDriver() {

		PreparedStatement ps = null;
		ResultSet resultSet = null;
		Connection con = datasource
				.getMysqlConnection();

		List<DriverBean> list = new LinkedList<>();
		Gson gson = new Gson();

		try {
			ps = con
					.prepareStatement(
							"SELECT driverName,lat,longitude,id FROM driver WHERE driverStatus=\"AVAILABLE\"");
			resultSet = ps.executeQuery();
			while (resultSet.next()) {

				DriverBean driverBean = new DriverBean();

				driverBean.setName(resultSet.getString(1));
				driverBean.setLat(resultSet.getString(2));
				driverBean.setLongitude(resultSet.getString(3));
				driverBean.setId(resultSet.getInt(4));
				list.add(driverBean);
			}

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

		return "p(" + gson.toJson(list) + ")";
	}

	@RequestMapping(value = HandlerConstants.GET_DRIVER, method = RequestMethod.GET)
	public @ResponseBody String getDriver(@PathVariable("id") String id) {

		PreparedStatement ps = null;
		ResultSet resultSet = null;
		Connection con = datasource
				.getMysqlConnection();

		DriverBean driverBean = new DriverBean();
		Gson gson = new Gson();

		try {
			ps = con
					.prepareStatement(
							"SELECT driverName,lat,longitude,id FROM driver WHERE id=?");
			ps.setInt(1, Integer.parseInt(id));
			resultSet = ps.executeQuery();
			while (resultSet.next()) {

				driverBean.setName(resultSet.getString(1));
				driverBean.setLat(resultSet.getString(2));
				driverBean.setLongitude(resultSet.getString(3));
				driverBean.setId(resultSet.getInt(4));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			;
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
		return "p(" + gson.toJson(driverBean) + ")";
	}

	@RequestMapping(value = HandlerConstants.GET_AlL_CUSTOMER, method = RequestMethod.GET)
	public @ResponseBody String getAllCustomer() {

		PreparedStatement ps = null;
		ResultSet resultSet = null;
		Connection con = datasource
				.getMysqlConnection();

		List<CustomerBean> list = new LinkedList<>();
		Gson gson = new Gson();

		try {
			ps = con.prepareStatement(
					"SELECT customerName,lat,longitude,id FROM customer");
			resultSet = ps.executeQuery();
			while (resultSet.next()) {

				CustomerBean customerBean = new CustomerBean();

				customerBean.setName(resultSet.getString(1));
				customerBean.setLat(resultSet.getString(2));
				customerBean.setLongitude(resultSet.getString(3));
				customerBean.setId(resultSet.getInt(4));

				list.add(customerBean);
			}

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

		return "p(" + gson.toJson(list) + ")";
	}

	@RequestMapping(value = HandlerConstants.RESET, method = RequestMethod.GET)
	public @ResponseBody String reset() {

		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		Connection con = datasource
				.getMysqlConnection();

		Boolean status = false;
		Gson gson = new Gson();

		try {
			ps = con.prepareStatement(
					"DELETE FROM customer");
			ps.execute();

			ps2 = con.prepareStatement(
					"UPDATE driver SET driverStatus=\"AVAILABLE\"");
			Integer count = ps2.executeUpdate();
			if (count > 0) {
				status = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				ps2.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("Status", status);

		return gson.toJson(jsonObject);
	}
}