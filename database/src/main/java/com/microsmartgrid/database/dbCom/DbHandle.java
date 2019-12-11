package com.microsmartgrid.database.dbCom;

import com.microsmartgrid.database.dbDataStructures.AbstractDevice;
import com.microsmartgrid.database.dbDataStructures.AdditionalDeviceInformation;
import com.microsmartgrid.database.dbDataStructures.DaiSmartGrid.Readings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * Connection to PostgreSQL database
 */
public class DbHandle {

	private static final Logger logger = LogManager.getLogger(DbHandle.class.getName());
	private static final String INSERT_READINGS_SQL = "INSERT INTO " +
		"readings " +
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	private static final String INSERT_DEVICES_SQL = "INSERT INTO " +
		"devices " +
		"VALUES (DEFAULT, ?, ?, ?, ?, ?);";
	private static final String QUERY_DEVICES_SQL = "SELECT * FROM devices WHERE name=?;";


	/**
	 * Execute SQL command
	 *
	 * @param command
	 */
	public void execute(String command) throws SQLException {
		try(Connection conn = DriverManager.getConnection("jdbc:h2:./test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE", "sa", "");
			Statement stmt = conn.createStatement()){
			stmt.execute(command);
		} catch (SQLException e) {
			logger.error("Couldn't excecute: " + command);
			logger.error(e);
		}
	}

	/**
	 * Insert AdditionalDeviceInformation object into database
	 *
	 * @param deviceInfo
	 */
	public void insertDeviceInfo(AdditionalDeviceInformation deviceInfo) throws SQLException {
		try (Connection conn = DriverManager.getConnection("jdbc:h2:./test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE", "sa", "");
			 PreparedStatement info = conn.prepareStatement(INSERT_DEVICES_SQL);) {

			info.setString(1, deviceInfo.getName());
			info.setString(2, deviceInfo.getDescription());
			info.setString(3, deviceInfo.getType().toString());
			info.setString(4, deviceInfo.getSubtype().toString());
			info.setArray(5, conn.createArrayOf("INTEGER", deviceInfo.getChildren()));

			info.executeUpdate();
			conn.commit();

		} catch (SQLException e) {
			logger.warn("Couldn't commit deviceInformation with name " + deviceInfo.getName() + " to database.");
			e.printStackTrace();
		}
	}


	/**
	 * Insert Readings object into database
	 *
	 * @param device
	 */
	public void insertReadings(Readings device) throws SQLException {
		try (Connection conn = DriverManager.getConnection("jdbc:h2:./test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE", "sa", "");
			 PreparedStatement reading = conn.prepareStatement(INSERT_READINGS_SQL)) {

			reading.setTimestamp(1, Timestamp.from(device.getTimestamp()));
			reading.setInt(2, device.getId());
			reading.setFloat(3, device.getActive_energy_A_minus());
			reading.setFloat(4, device.getActive_energy_A_plus());
			reading.setFloat(5, device.getReactive_energy_R_minus());
			reading.setFloat(6, device.getReactive_energy_R_plus());
			reading.setFloat(7, device.getActive_power_P_total());
			reading.setFloat(8, device.getActive_power_P_total());
			reading.setFloat(9, device.getActive_power_P1());
			reading.setFloat(10, device.getActive_power_P2());
			reading.setFloat(11, device.getActive_power_P3());
			reading.setFloat(12, device.getReactive_power_Q_total());
			reading.setFloat(13, device.getReactive_power_Q1());
			reading.setFloat(14, device.getReactive_power_Q2());
			reading.setFloat(15, device.getReactive_power_Q3());
			reading.setFloat(16, device.getApparent_power_S_total());
			reading.setFloat(17, device.getApparent_power_S1());
			reading.setFloat(18, device.getApparent_power_S2());
			reading.setFloat(19, device.getApparent_power_S3());
			reading.setFloat(20, device.getCurrent_I_avg());
			reading.setFloat(21, device.getCurrent_I1());
			reading.setFloat(22, device.getCurrent_I2());
			reading.setFloat(23, device.getCurrent_I3());
			reading.setFloat(24, device.getVoltage_U_avg());
			reading.setFloat(25, device.getVoltage_U1());
			reading.setFloat(26, device.getVoltage_U2());
			reading.setFloat(27, device.getVoltage_U3());
			reading.setFloat(28, device.getFrequency_grid());

			reading.executeUpdate();
			conn.commit();

		} catch (SQLException e) {
			logger.warn("Couldn't commit reading connected to device with id " +
				(device.getId() > 0 ? device.getId() : "none (no device information set yet)") + " to database");
			e.printStackTrace();
		}
	}


	/**
	 * Query devices table
	 * @param topic
	 * @return
	 */
	public AdditionalDeviceInformation queryDevices(String topic){
		AdditionalDeviceInformation info = null;
		try(Connection conn = DriverManager.getConnection("jdbc:h2:./test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE", "sa", "");
			PreparedStatement stmt = conn.prepareStatement(QUERY_DEVICES_SQL,Statement.RETURN_GENERATED_KEYS)){

			stmt.setString(1,"'" + topic + "'");
			ResultSet rs = stmt.executeQuery();

			if(!rs.next()) throw new SQLException("No entry found with name='" + topic + "'");

			info = new AdditionalDeviceInformation(rs.getString(2));
			info.setDescription(rs.getString(3));
			info.setType(AdditionalDeviceInformation.Type.valueOf(rs.getString(4)));
			info.setSubtype(AdditionalDeviceInformation.Subtype.valueOf(rs.getString(5)));
			info.setChildren((Integer[])rs.getArray(6).getArray());

			rs.close();

		}catch(SQLException e){
			logger.warn("Could not fetch device info with name " + topic);
			e.printStackTrace();
		}
		return info;
	}

}
