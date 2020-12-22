package net.blastmc.onyx.api.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SQLHelper {

    private HikariDataSource source;

    public SQLHelper(String url, String user, String passwd, String database) {
        HikariConfig config;
        try {
            config = new HikariConfig();
        } catch (LinkageError ex) {
            handleLinkageError(ex);
            throw ex;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        config.setPoolName("onyx-hikari");
        config.setJdbcUrl(String.format("jdbc:mysql://%s/%s"
                , url, database));
        config.setUsername(user);
        config.setPassword(passwd);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        config.addDataSourceProperty("alwaysSendSetIsolation", "false");
        config.addDataSourceProperty("cacheCallableStmts", "true");
        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("characterEncoding", "utf-8");
        config.addDataSourceProperty("useSSL", "false");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(10);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(5000);
        Log.getLogger().sendLog("§e连接 " + config.getJdbcUrl() + " 中。。。");
        this.source = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        if (this.source == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }
        Connection connection = this.source.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }
        return connection;
    }

    private static void handleLinkageError(LinkageError linkageError) {
        Log.getLogger().sendLog("在引入 Hikari 时发生错误 " + linkageError.getClass().getSimpleName() + "。 Onyx 似乎与其他插件冲突！");
    }

    public void create(String table, Value... values) {
        try {
            Connection conn = getConnection();
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
            PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + "(" + sb.toString() + ");");
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getOrder(String table, String flag, String flagData, String data){
        try {
            Connection conn = getConnection();
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("select data_order from (SELECT t.*, @rownum := @rownum + 1 AS data_order FROM (select * from " + table + " order by " + data + " desc) t, (SELECT @rownum := 0) r) b where " + flag + " = '" + flagData + "';");
            if (rs.next()) {
                return rs.getInt(1);
            }
            rs.close();
            s.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public List getColumnData(String table, String flag, String flagData, int line){
        List<Object> list = Lists.newArrayList();
        try {
            Connection conn = getConnection();
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM " + table + " WHERE " + flag + " = '" + flagData + "';");
            while (rs.next()) {
                list.add(rs.getObject(line));
            }
            rs.close();
            s.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List getRowData(String table, int line) {
        List<Object> list = Lists.newArrayList();
        try {
            Connection conn = getConnection();
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
            rs.close();
            s.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean checkDataExists(String table, String flag, String data){
        boolean b = false;
        try {
            Connection conn = getConnection();
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + table + " WHERE " + flag + " = '" + data + "';");
            while (rs.next()) {
                int count = rs.getInt(1);
                b = count != 0;
            }
            rs.close();
            s.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return b;
    }

    public List getData(String query, String data){
        List<Object> list = Lists.newArrayList();
        try {
            Connection conn = getConnection();
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                list.add(rs.getObject(data));
            }
            rs.close();
            s.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List getData(String table, String flag, String flagData, String... datas) {
        List<Object> list = Lists.newArrayList();
        try {
            Connection conn = getConnection();
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
            rs.close();
            s.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void putData(String table, String flag, String flagData, SqlValue... values) {
        try {
            Connection conn = getConnection();
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
                conn.close();
            } else {
                PreparedStatement stmt = conn.prepareStatement("UPDATE " + table + " SET " + sb.toString() + " WHERE " + flag + " = '" + flagData + "';");
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertData(String table, SqlValue... values) {
        try {
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO " + table +
                            " (" + Joiner.on(", ").join(Arrays.stream(values).map(SqlValue::getData).collect(Collectors.toList())) + ") VALUES ('" +
                            Joiner.on("', '").join(Arrays.stream(values).map(SqlValue::getValue).collect(Collectors.toList())) + "');");
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countData(String query){
        try {
            Connection conn = getConnection();
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(query);
            if (rs.next()) {
                int count = rs.getInt(1);
                return count;
            }
            rs.close();
            s.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void close(){
        this.source.close();
    }

    public List<Object> getListData(String table, String flag, String flagData, String data) {
        List<Object> list = Lists.newArrayList();
        try {
            Connection conn = getConnection();
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT " + data + " FROM " + table + " WHERE " + flag + " = '" + flagData + "';");
            while (rs.next()) {
                list.add(rs.getObject(1));
            }
            rs.close();
            s.close();
            conn.close();
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
