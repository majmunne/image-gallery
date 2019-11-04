package imggallery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;

@Path("image")
public class ImageGalleryService {

    @Path("get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllImages() {
        Connection connection = getRemoteConnection();
        Statement statement = null;
        String query = "select * from image";

        try {
            statement = connection != null ? connection.createStatement() : null;
            ResultSet resultSet = statement != null ? statement.executeQuery(query) : null;
            ObjectMapper objectMapper = new ObjectMapper();
            String imageListJson = "";
            try {
                imageListJson = objectMapper.writeValueAsString(getImageList(resultSet));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return Response.status(Response.Status.OK).entity(imageListJson).type(MediaType.APPLICATION_JSON).build();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private ArrayList<ImageEntity> getImageList(ResultSet resultSet) {
        ArrayList<ImageEntity> imageList = new ArrayList<>();

        try {
            while (resultSet.next()) {
                ImageEntity imageEntity = new ImageEntity(resultSet.getInt("idimage"), resultSet.getString("image"));
                imageList.add(imageEntity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return imageList;
    }

    private static Connection getRemoteConnection() {
        if (System.getProperty("RDS_HOSTNAME") != null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String dbName = System.getProperty("RDS_DB_NAME");
                String userName = System.getProperty("RDS_USERNAME");
                String password = System.getProperty("RDS_PASSWORD");
                String hostname = System.getProperty("RDS_HOSTNAME");
                String port = System.getProperty("RDS_PORT");
                String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
                Connection con = DriverManager.getConnection(jdbcUrl);
                return con;
            }
            catch (ClassNotFoundException | SQLException e) { e.printStackTrace();}
        }
        return null;
    }
}
