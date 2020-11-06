package net.blastmc.onyx.shared.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SQLHelper {
    
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

    public void create(String table, Value... values) {
        try {
            if (conn.isClosed()) {
                exeConn();
            }
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for(Value value : values){
                if(!first){
                    sb.append(", ");
                }else{
                    first = false;
                }
                sb.append(value.getName() + " " + value.getType().getStatement());
            }
            conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + table + "(" + sb.toString() + ");");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDoubleColumn(String table, String name) {
        try {
            if (conn.isClosed()) {
                exeConn();
            }
            Statement s = conn.createStatement();
            s.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + name + " DOUBLE;");
        } catch (SQLException ignored) {
        }
    }

    public void addIntegerColumn(String table, String name) {
        try {
            if (conn.isClosed()) {
                exeConn();
            }
            Statement s = conn.createStatement();
            s.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + name + " INTEGER;");
        } catch (SQLException ignored) {
        }
    }

    public void addStringColumn(String table, String name) {
        try {
            if (conn.isClosed()) {
                exeConn();
            }
            Statement s = conn.createStatement();
            s.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + name + " VARCHAR(200) CHARACTER SET utf8;");
        } catch (SQLException ignored) {
        }
    }

    public int getOrder(String table, String flag, String flagData, String data){
        try {
            if (conn.isClosed()) {
                exeConn();
            }
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


    public List getColumnData(String table, String flag, String flagData, int line){
        List<Object> list = Lists.newArrayList();
        try {
            if (conn.isClosed()) {
                exeConn();
            }
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

    public List getRowData(String table, int line) {
        List<Object> list = Lists.newArrayList();
        try {
            if (conn.isClosed()) {
                exeConn();
            }
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM " + table + ";");
            int j = 1;
            while (rs.next()) {
                if (j == line) {
                    int i = 1;
                    while(true) {
                        try {
                            list.add(rs.getObject(i));
                        }catch (SQLException ex){
                            break;
                        }
                        i++;
                    }
                    break;
                }
                j++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean checkDataExists(String query){
        boolean b = false;
        try {
            if(this.getConnection().isClosed()){
                this.exeConn();
            }
            Statement s = this.getConnection().createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                int count = rs.getInt(1);
                b = count != 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return b;
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

    public ResultSet queryData(String query){
        try {
            if (conn.isClosed()) {
                exeConn();
            }
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(query);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List getData(String query, String data){
        List<Object> list = Lists.newArrayList();
        try {
            if (conn.isClosed()) {
                exeConn();
            }
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                list.add(rs.getObject(data));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List getData(String table, String flag, String flagData, String... datas) {
        List<Object> list = Lists.newArrayList();
        try {
            if (conn.isClosed()) {
                exeConn();
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < datas.length; i++) {
                if (i != 0)
                    sb.append(", ");
                sb.append(datas.clone()[i]);
            }
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT " + sb.toString() + " FROM " + table + " WHERE " + flag + " = '" + flagData + "';");
            if (rs.next()) {
                for (int i = 0; i < datas.length; i++) {
                    Object obj = rs.getObject(i + 1);
                    list.add(obj == null ? "" : obj);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void putData(String table, String flag, String flagData, SqlValue... values) {
        try {
            if (conn.isClosed()) {
                exeConn();
            }
            StringBuilder datas = new StringBuilder();
            datas.append(flag + ", ");
            for (int i = 0; i < values.length; i++) {
                if (i != 0)
                    datas.append(", ");
                datas.append(values.clone()[i].data);
            }

            StringBuilder sqlValues = new StringBuilder();
            sqlValues.append("'").append(flagData).append("', ");
            for (int i = 0; i < values.length; i++) {
                if (i != 0)
                    sqlValues.append(", ");
                sqlValues.append("'").append(values.clone()[i].value).append("'");
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                if (i != 0)
                    sb.append(", ");
                sb.append(values.clone()[i].data).append(" = '" + values.clone()[i].value + "'");
            }
            if (!checkDataExists(table, flag, flagData)) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + table + " (" + datas.toString() + ") VALUES(" + sqlValues.toString() + ");");
                stmt.executeUpdate();
                stmt.close();
            } else {
                PreparedStatement stmt = conn.prepareStatement("UPDATE " + table + " SET " + sb.toString() + " WHERE " + flag + " = '" + flagData + "';");
                stmt.executeUpdate();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertData(String table, SqlValue... values) {
        //Object[] data, Object[] value
        try {
            if(this.getConnection().isClosed()){
                this.exeConn();
            }
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO " + table +
                            " (" + Joiner.on(", ").join(Arrays.stream(values).map(SqlValue::getData).collect(Collectors.toList())) + ") VALUES ('" +
                            Joiner.on("', '").join(Arrays.stream(values).map(SqlValue::getValue).collect(Collectors.toList())) + "');");
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countData(String query){
        try {
            if(this.getConnection().isClosed()){
                this.exeConn();
            }
            Statement s = this.getConnection().createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                int count = rs.getInt(1);
                return count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Object> getListData(String table, String flag, String flagData, String data) {
        List<Object> list = Lists.newArrayList();
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
                list.add(rs.getObject(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list.isEmpty() ? null : list;
    }

    public static class SqlValue{

        private String data;
        private Object value;

        public SqlValue(String data, Object value) {
            this.data = data;
            this.value = value;
        }

        public String getData() {
            return data;
        }

        public Object getValue() {
            return value;
        }

    }

    public static class Value{

        private ValueType type;
        private String name;

        public Value(ValueType type, String name) {
            this.type = type;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public ValueType getType() {
            return type;
        }

    }

    public enum ValueType{

        ID,
        STRING,
        DECIMAL,
        INTEGER;

        public String getStatement(){
            switch (this){
                case ID:
                    return "INT NOT NULL AUTO_INCREMENT PRIMARY KEY";
                case STRING:
                    return "VARCHAR(200) CHARACTER SET utf8";
                case DECIMAL:
                    return "DOUBLE";
                case INTEGER:
                    return "INTEGER";
            }
            return null;
        }
    }

}
