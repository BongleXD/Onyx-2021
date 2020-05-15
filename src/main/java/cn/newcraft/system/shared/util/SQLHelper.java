package cn.newcraft.system.shared.util;

import com.google.common.collect.Lists;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class SQLHelper {

    private static SQLHelper sql;
    private String url;
    private String user;
    private String passwd;
    private String database;
    private Connection conn;

    public SQLHelper(String url, String user, String passwd, String database) {
        this.url = url;
        this.user = user;
        this.passwd = passwd;
        this.database = database;
        exeConn();
    }

    public Connection getConnection() {
        return this.conn;
    }

    public void exeConn() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + url + "/" + database + "?autoReconnect=true&user=" + user + "&password=" + passwd + "&useSSL=false" + "&useUnicode=true&characterEncoding=UTF-8");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void create(String table) {
        try {
            if (conn.isClosed()) {
                exeConn();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + table + "(id INT NOT NULL AUTO_INCREMENT PRIMARY KEY);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addStringColumn(String table, String name) {
        try {
            if (conn.isClosed()) {
                exeConn();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            Statement s = conn.createStatement();
            s.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + name + " VARCHAR(200) CHARACTER SET utf8;");
        } catch (SQLException ignored) {
        }
    }

    public void putFlag(String table, String flag, String flagData) {
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + table + " SET " + flag + " = ?;");
            stmt.setObject(1, flagData);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getOrder(String table, String flag, String flagData, String data){
        try {
            if (conn.isClosed()) {
                exeConn();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("select data_order from (SELECT t.*, @rownum := @rownum + 1 AS data_order FROM (select * from " + table + " order by " + data + " desc) t, (SELECT @rownum := 0) r) b where " + flag + " = '" + flagData + "';");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List getAllData(String table, String flag, String flagData, int line){
        List list = Lists.newArrayList();
        try {
            if (conn.isClosed()) {
                exeConn();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM " + table + " WHERE " + flag + " = '" + flagData + "';");
            while (rs.next()) {
                list.add(rs.getObject(line));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Object[] getAllData(String table, int line, int length) {
        Object[] array = new Object[length];
        try {
            if (conn.isClosed()) {
                exeConn();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM " + table + ";");
            int j = 1;
            while (rs.next()) {
                if (j == line) {
                    for (int i = 0; i < length; i++) {
                        array[i] = rs.getObject(i + 2);
                    }
                    break;
                }
                j++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return array;
    }

    public boolean checkDataExists(String table, String flag, String data){
        boolean b = false;
        try {
            if(this.getConnection().isClosed()){
                this.exeConn();
            }
            Statement s = this.getConnection().createStatement();
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + table + " WHERE " + flag + " = '" + data + "';");
            while (rs.next()) {
                int count = rs.getInt(1);
                b = count != 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return b;
    }

    public void addDoubleColumn(String table, String name) {
        try {
            if (conn.isClosed()) {
                exeConn();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            Statement s = conn.createStatement();
            s.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + name + " DOUBLE;");
        } catch (SQLException ignored) {
        }
    }


    public Object getData(String flag, String flagData, String table, String data) {
        try {
            if (conn.isClosed()) {
                exeConn();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT " + data + " FROM " + table + " WHERE " + flag + " = '" + flagData + "';");
            while (rs.next()) {
                return rs.getObject(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void putData(String table, String flag, String flagData, String data, Object value) {
        try {
            if (!conn.prepareStatement("SELECT id FROM " + table + " WHERE " + flag + " = '" + flagData + "';").executeQuery().next()) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + table + " (" + data + ") " + " VALUE(?);");
                stmt.setObject(1, value);
                stmt.executeUpdate();
                stmt.close();
            } else {
                PreparedStatement stmt = conn.prepareStatement("UPDATE " + table + " SET " + data + " = ? WHERE " + flag + " = '" + flagData + "';");
                stmt.setObject(1, value);
                stmt.executeUpdate();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addIntegerColumn(String table, String name) {
        try {
            if (conn.isClosed()) {
                exeConn();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            Statement s = conn.createStatement();
            s.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + name + " INTEGER;");
        } catch (SQLException ignored) {
        }
    }

    public void insertData(String table, Object[] data, Object[] value) {
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + table + " (" + Arrays.toString(data).replace("[", "").replace("]", "") + ") " + " VALUES (" +  Arrays.toString(value)
                    .replace("[", "'")
                    .replace("]", "'")
                    .replace(", ", "', '") + ");");
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void putData(String table, String pid, String data, Object value) {
        try {
            if (!conn.prepareStatement("SELECT id FROM " + table + " WHERE pid = '" + pid + "';").executeQuery().next()) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + table + " (" + data + ") " + " VALUE(?);");
                stmt.setObject(1, value);
                stmt.executeUpdate();
                stmt.close();
            } else {
                PreparedStatement stmt = conn.prepareStatement("UPDATE " + table + " SET " + data + " = ? WHERE pid = '" + pid + "';");
                stmt.setObject(1, value);
                stmt.executeUpdate();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
