package com.nsw.baseballnsw.api;


import com.nsw.baseballnsw.models.Article;
import com.nsw.baseballnsw.models.ArticleComment;
import com.nsw.baseballnsw.models.ArticleResponse;
import com.nsw.baseballnsw.models.ClubResponse;
import com.nsw.baseballnsw.models.Event;
import com.nsw.baseballnsw.models.EventResponse;
import com.nsw.baseballnsw.models.Folder;
import com.nsw.baseballnsw.models.Group;
import com.nsw.baseballnsw.models.GroupFoldersRes;
import com.nsw.baseballnsw.models.GroupResponse;
import com.nsw.baseballnsw.models.LaddersResponse;
import com.nsw.baseballnsw.models.MediaAlbum;
import com.nsw.baseballnsw.models.MediaAlbumResponse;
import com.nsw.baseballnsw.models.Member;
import com.nsw.baseballnsw.models.Notification;
import com.nsw.baseballnsw.models.NotificationResponse;
import com.nsw.baseballnsw.models.Profile;
import com.nsw.baseballnsw.models.Register;
import com.nsw.baseballnsw.models.Token;
import com.nsw.baseballnsw.models.VideoAlbum;
import com.nsw.baseballnsw.models.VideoAlbumResponse;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

public interface API {

    @FormUrlEncoded
    @POST("/token")
    // public void login(@Field("first") String firstF, Callback<Token> callback);
    public void login(@Field("grant_type") String grant_type,
                      @Field("username") String username,
                      @Field("password") String password,
                      Callback<Token> callback);

    //new api v2
    @POST("/apiv2/account/register")
    public void register(@Header("Authorization") String auth,
                         @Body Register registerModel,
                         Callback<Response> callback);


    @FormUrlEncoded
    @POST("/apiv2/account/forgotpassword")
    public void forgetPassword(@Header("Authorization") String auth,
                                @Field("email") String email,
                                Callback<Response> callback);

    @FormUrlEncoded
    @POST("/apiv2/account/changepassword")
    public void postNewPassword(@Header("Authorization")String auth,
                                @Field("oldPassword") String oldPassword,
                                @Field("newPassword") String newPassword,
                                @Field("confirmPassword") String confirmPassword,
                                Callback<Response> callback);

    //new api v2
    @FormUrlEncoded
    @POST("/apiv2/notifications/registerDeviceForPush")
    public void registerDeviceForPushs(@Header("Authorization") String auth,
                                       @Field("DeviceType") int deviceType,
                                       @Field("DeviceId") String gcmToken,
                                       @Field("MemberId") int memberID,
                                       Callback<Response> callback);


    @GET("/apiv2/groupmembers/searchForInvitedGroups/{email}/")
    public void getInvitedGrouping(@Path("email") String email, Callback<Response> response);

    @PUT("/apiv2/Account/memberdetails")
    public void putMember(@Header("Authorization") String auth,
                          @Body Member member,
                          Callback<Response> callback);

    @GET("/apiv2/account/memberdetails")
    public void getMemberDetailing(@Header("Authorization") String auth,
                                   Callback<Profile> callback);

    //older version
    @GET("/apiv2/events/all")      //here is the other url part.best way is to start using /
    public void getAllEvents(@Header("Authorization") String auth, Callback<EventResponse> response);

    @GET("/apiv2/groupmembers/all")
    void getClubNames(Callback<ClubResponse> callback);


    @POST("/apiv2/events/save")
    public void postEvent(@Header("Authorization") String auth,
                           @Body Event eventModel,
                           Callback<Response> callback);

    //new
    @POST("/apiv2/events/save")
    public void postEvents(@Header("Authorization") String auth,
                           @Body Event eventModel,
                           Callback<Response> callback);

    @PUT("/apiv2/events/update")
    public void putEvents(@Header("Authorization") String auth,
                          @Body Event eventModel,
                          Callback<Response> callback);

    //older api version 1
    @GET("/api/groupMembers/groups")      //here is the other url part.best way is to start using /
    public void getAllGroups(@Header("Authorization") String auth, Callback<List<Group>> response);


    //new api v2
    @GET("/apiv2/groupMembers/groups")      //here is the other url part.best way is to start using /
    public void getAllGrouping(@Header("Authorization") String auth, Callback<GroupResponse> response);

    @GET("/apiv2/ladders/{groupID}")
    public void getLadders(@Header("Authorization") String auth,@Path("groupID") int groupID, Callback<LaddersResponse> response);

    //new api v2
    @GET("/apiv2/events/all")      //here is the other url part.best way is to start using /
    public void getAllEventings(@Header("Authorization") String auth, Callback<EventResponse> response);

    @GET("/apiv2/events/group/{groupID}")
    public void getFixtureGroup(@Header("Authorization") String auth,@Path("groupID") int groupID, Callback<EventResponse> response);
    //older api
    @GET("/api/events/{eventID}")
    public void getEvent(@Header("Authorization") String auth,@Path("eventID") int eventID, Callback<Event> response);

    @FormUrlEncoded
    @POST("/apiv2/events/comment")
    public void postEventComments(@Header("Authorization") String auth,
                                  @Field("EventId") int eventID,
                                  @Field("Comment") String comment,
                                  Callback<Response> callback);


    @FormUrlEncoded
    @POST("/apiv2/media/album")
    public void postMediaAlbum(@Header("Authorization") String auth,
                                @Field("name") String name,
                                @Field("groupid") int groupID,
                                Callback<Response> callback);

    @FormUrlEncoded
    @POST("/apiv2/media/album")
    public void postVideoAlbum(@Header("Authorization") String auth,
                               @Field("name") String name,
                               @Field("groupid") int groupID,
                               @Field("albumtype") String type,
                               Callback<Response> callback);

    @FormUrlEncoded
    @POST("/apiv2/media/postvideo")
    public void postVideoToAlbum(@Header("Authorization") String auth,
                                 @Field("mediaAlbumId") int mediaAlbumID,
                                 @Field("videourl") String url,
                                 Callback<Response> callback);

    @FormUrlEncoded
    @POST("/apiv2/groupmembers/invite")
    public void postInviteUsers(@Header("Authorization") String auth,
                                @Field("Name") String name,
                                @Field("EmailAddress") String email,
                                @Field("InviteAgain") boolean inviteAgain,
                                @Field("GroupId") int groupID,
                                Callback<Response> callback);

    //new api v2
    @GET("/apiv2/articles/get/{groupID}")
    public void getGroupArticlesnew(@Header("Authorization") String auth, @Path("groupID") int groupID, Callback<ArticleResponse> response);

    //older api
    @GET("/apiv2/Articles/{articleID}")
    public void getArticle(@Header("Authorization") String auth,
                           @Path("articleID") int articleID,
                           Callback<Article> response);

    //new api v2
    @GET("/apiv2/Articles/{articleID}")
    public void getArticles(@Header("Authorization") String auth,
                            @Path("articleID") int articleID,
                            Callback<Article> response);

    //new api v2
    @FormUrlEncoded
    @POST("/apiv2/articles/Comment/{groupID}/{articleID}")
    public void postArticleComments(@Header("Authorization") String auth,
                                    @Path("groupID") int  groupID,
                                    @Path("articleID") int articleID,
                                    @Field("ArticleCommentDescription") String comment,
                                    Callback<ArticleComment> callback);
    //older
    @POST("/api/folder/Create")
    public void postFolder(@Header("Authorization") String auth,
                           @Body Folder registerModel,
                           Callback<Response> callback);

    //new api v2
    @POST("/apiv2/folder/Create")
    public void postFolders(@Header("Authorization") String auth,
                            @Body Folder registerModel,
                            Callback<Response> callback);

    @POST("/apiv2/notifications/add")
    public void postNotifications(@Header("Authorization") String auth,
                                  @Body Notification notificationModel,
                                  Callback<Response> callback);


    //older
    @GET("/api/media/album/{id}")
    public void getMediaAlbum(@Header("Authorization") String auth, @Path("id") int mediaAlbumID, Callback<MediaAlbum> response);

    //new api v2
    @GET("/apiv2/media/album/{id}")
    public void getMediaAlbums(@Header("Authorization") String auth, @Path("id") int mediaAlbumID, Callback<MediaAlbumResponse> response);


    @GET("/apiv2/video/album/{id}")
    public void getVideoAlbum(@Header("Authorization") String auth, @Path("id") int mediaAlbumID, Callback<MediaAlbum> response);


    @FormUrlEncoded
    @POST("/apiv2/notifications/comment")
    public void postNotificationComments(@Header("Authorization") String auth,
                                         @Field("NotificationId") int notificationID,
                                         @Field("Comment") String comment,
                                         Callback<Response> callback);

    //new api v2
    @GET("/apiv2/notifications/get/{groupID}")
    public void getGroupNotificationsnew(@Header("Authorization") String auth, @Path("groupID") int groupID, Callback<NotificationResponse> response);

    //api v2
    @FormUrlEncoded
    @POST("/apiv2/media/Comment")
    public void postMediaComments(@Header("Authorization") String auth,
                                  @Field("MediaId") int  mediaID,
                                  @Field("Comment") String comment,
                                  Callback<Response> callback);

    //new api v2
    @FormUrlEncoded
    @PUT("/apiv2/media/album")
    public void putMediaAlbums(@Header("Authorization") String auth,
                               @Field("name") String name,
                               @Field("description") String description,
                               @Field("mediaAlbumId") int mediaAlbumID,
                               Callback<Response> callback);

    @Multipart
    @POST("/api/media/postImage/{albumID}")
    public void postImageToAlbum(@Header("Authorization") String auth,
                                 @Path("albumID") int albumID,
                                 @Part("image") TypedFile file,
                                 Callback<Response> response);

    @Multipart
    @POST("/apiv2/media/postimage/{albumID}")
    public void postImageToAlbums(@Header("Authorization") String auth,
                                  @Path("albumID") int albumID,
                                  @Part("image") TypedFile file,
                                  Callback<Response> response);

    //new api v2
    @GET("/apiv2/media/get/{groupID}")
    public void getGroupingMediaAlbums(@Header("Authorization") String auth, @Path("groupID") int groupID, Callback<MediaAlbumResponse> response);

    @GET("/apiv2/video/get/{groupID}")
    public void getGroupingVideoAlbum(@Header("Authorization") String auth, @Path("groupID") int groupID, Callback<MediaAlbumResponse> response);



    //new api v2
    @GET("/apiv2/document/get/{groupID}")
    public void getGroupFoldersnew(@Header("Authorization") String auth, @Path("groupID") int groupID, Callback<GroupFoldersRes> response);

    //older
    @PUT("/apiv2/folder/Update")
    public void putFolders(@Header("Authorization") String auth,
                          @Body Folder registerModel,
                          Callback<Response> callback);


    //new api v2
    @GET("/apiv2/notifications/all")      //here is the other url part.best way is to start using /
    public void getAllNotificationsnew(@Header("Authorization") String auth, Callback<NotificationResponse> response);


    //new api v2
    @Multipart
    @POST("/apiV2/account/profileImage")
    public void postProfileImage(@Header("Authorization") String auth,
                                 @Part("image") TypedFile file,
                                 Callback<Response> response);


    @DELETE("/apiv2/events/delete/{eventID}")
    public void delete(@Header("Authorization") String auth,
                       @Path("eventID") int eventID,
                       Callback<Response> callback);

    @DELETE("/apiv2/media/delete/{MediaId}")
    public void deleteMediaItem(@Header("Authorization") String auth,
                                @Path("MediaId") int mediaID,
                                Callback<Response> callback);

    @POST("/apiv2/account/logout")
    public void logoutUser(@Header("Authorization") String auth,
                           Callback<Response> callback);

    @DELETE("/apiv2/notifications/delete/{notificationId}")
    public void notificationDelete(@Header("Authorization") String auth,
                                   @Path("notificationId") int notificationID,
                                   Callback<Response> callback);

    @DELETE("/apiv2/notifications/comment/delete/{notificationCommentId}")
    public void CommentsDelete(@Header("Authorization") String auth,
                               @Path("notificationCommentId") int notificationCommentId,
                               Callback<Response> callback);

    @DELETE("/apiv2/media/comment/delete/{mediacommentid}")
    public void mediaCommentdelete(@Header("Authorization") String auth,
                                   @Path("mediacommentid") int mediacommentid,
                                   Callback<Response> callback);

    @DELETE("/apiv2/media/comment/delete/{eventCommentId}")
    public void eventCommentdelete(@Header("Authorization") String auth,
                                   @Path("eventCommentId") int eventCommentId,
                                   Callback<Response> callback);

    @FormUrlEncoded
    @POST("/apiv2/groupmembers/creategroup")
    public void creategroup(@Header("Authorization") String auth,
                            @Field("groupname") String name,
                            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/apiv2/groupmembers/renamegroup")
    public void putGroupname(@Header("Authorization") String auth,
                             @Field("groupid") int groupId,
                             @Field("groupname") String groupName,
                             Callback<Response> callback);
}
