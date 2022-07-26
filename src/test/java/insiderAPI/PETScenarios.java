package insiderAPI;

import Pet.*;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.testng.AssertJUnit.*;

public class PETScenarios {
    Pet myPet = new Pet();
    int id;

    public void initializePet(){
        Category myCategory = new Category();
        myCategory.setId(1111);
        myCategory.setName("thisIsCategory");
        Tag myTag = new Tag();
        myTag.setId(2222);
        myTag.setName("thisIsTag");
        myPet.setId(123123);
        myPet.setCategory(myCategory);
        myPet.setName("thisIsPetName");
        List<String> photoURLS = new ArrayList<>();
        photoURLS.add("helloTest.jpg");
        myPet.setPhotoUrls(photoURLS);
        List<Tag> tagList = new ArrayList<Tag>();
        tagList.add(myTag);
        myPet.setTags(tagList);
        myPet.setStatus("available");
    }

    public void verifyJsonWithPet (JsonPath jsonPath, Pet pet){
        assertEquals(jsonPath.getInt("id"),pet.getId());
        assertEquals(jsonPath.getInt("category.id"),pet.getCategory().getId());
        assertEquals(jsonPath.getString("category.name"),pet.getCategory().getName());
        assertEquals(jsonPath.getList("photoUrls"),pet.getPhotoUrls());
        assertEquals(jsonPath.getInt("tags[0].id"),pet.getTags().get(0).getId());
        assertEquals(jsonPath.getString("tags[0].name"),pet.getTags().get(0).getName());
        assertEquals(jsonPath.getString("status"),pet.getStatus());
    }

    public void verifyGetWithQueryParam(String expectedStatus){
        Map<String,String> oneQueryMap = new HashMap<>();
        oneQueryMap.put("status",expectedStatus);
        Response response = given().accept(ContentType.JSON)
                .queryParams(oneQueryMap)
                .get("/pet/findByStatus");
        assertEquals(response.statusCode(),200);
        List<String> statusList = response.jsonPath().getList("status");
        for (String actualStatus : statusList) {
            assertEquals(actualStatus,expectedStatus);
        }
    }

    @BeforeTest
    public void setup(){
        baseURI = "https://petstore.swagger.io/v2";
    }

    @Test(priority = 0)
    public void createAPetPositive(){
        initializePet();
        Response post = given().contentType(ContentType.JSON)
                .body(myPet)
                .post("/pet");
        assertEquals(post.statusCode(),200);
        JsonPath jsonPath = post.jsonPath();
        verifyJsonWithPet(jsonPath,myPet);
        id = jsonPath.getInt("id");
    }

    @Test(priority = 1)
    public void getAPetPositive(){
        Response response = given().accept(ContentType.JSON)
                .pathParam("id", id)
                .get("/pet/{id}");
        assertEquals(200,response.statusCode());
        verifyJsonWithPet(response.jsonPath(),myPet);
    }

    @Test(priority = 2)
    public void updatePetPositive(){
        myPet.setName("UPDATEDName");
        Response put = given().contentType(ContentType.JSON)
                .body(myPet)
                .put("/pet");
        assertEquals(200,put.statusCode());
        verifyJsonWithPet(put.jsonPath(),myPet);
    }

    @Test(priority = 3)
    public void deletePetPositive(){
        Response delete = given().accept(ContentType.JSON)
                .pathParam("id", this.id)
                .delete("/pet/{id}");
        assertEquals(200,delete.statusCode());
        assertEquals(id,delete.jsonPath().getInt("message"));
    }

    @Test(priority = 4)
    public void getByByQueryParam(){
        List<String> statusList = new ArrayList<>();
        statusList.add("available");
        statusList.add("pending");
        statusList.add("sold");
        for (String oneStatus : statusList) {
            verifyGetWithQueryParam(oneStatus);
        }
    }

    //send a get request with invalid query params
    @Test(priority = 5)
    public void invalidGetByQueryParam(){
        Response response = given().accept(ContentType.JSON)
                .queryParams("insider", "insiderCaseStudy")
                .get("/pet/findByStatus");
        assertEquals(200,response.statusCode());
        assertEquals("[]",response.asString());
    }

    //send a get request for deleted pet
    @Test(priority = 6)
    public void invalidGetRequest(){
        given().accept(ContentType.JSON)
                .pathParam("id",id)
                .get("/pet/{id}")
                    .then()
                        .assertThat().statusCode(404)
                        .and().body("message", Matchers.equalTo("Pet not found"));
    }

    //send post without body
    @Test(priority = 7)
    public void invalidPostRequest(){
        given().contentType(ContentType.JSON)
                .post("/pet")
                .then()
                    .assertThat().statusCode(405);
    }

    //send post with invalid body
    @Test(priority = 8)
    public void invalidPostRequest2(){
        Map<String,String> oneMap = new HashMap<>();
        oneMap.put("hello","world");
        given().body(oneMap)
                .post("/pet")
                .then()
                    .assertThat().statusCode(415);
    }

    //send invalid put request
    @Test(priority = 9)
    public void invalidUpdateRequest(){
        given().contentType(ContentType.JSON)
                .pathParam("id",5)
                .body(myPet)
                .put("/pet/{id}")
                .then()
                    .assertThat().statusCode(405);
    }

    //send delete request for non-existing pet (deleted before)
    @Test(priority = 10)
    public void invalidDeleteRequest(){
        given().accept(ContentType.JSON)
                .pathParam("id", id)
                .delete("/pet/{id}")
                .then()
                    .assertThat().statusCode(404);
    }


}
