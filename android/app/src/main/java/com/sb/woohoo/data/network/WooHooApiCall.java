package com.sb.woohoo.data.network;

import com.sb.woohoo.ui.main.fragments.buysim.buypackage.pojo.GetAllPackagesResponse;
import com.sb.woohoo.ui.main.fragments.buysim.cart.pojo.BuyPackageRequest;
import com.sb.woohoo.ui.main.fragments.buysim.cart.pojo.BuyPackageResponse;
import com.sb.woohoo.ui.main.fragments.buysim.emailverification.pojo.EmailIdRequest;
import com.sb.woohoo.ui.main.fragments.buysim.emailverification.pojo.OtpSendResponse;
import com.sb.woohoo.ui.main.fragments.buysim.otpverification.pojo.EmailIdOtpRequest;
import com.sb.woohoo.ui.main.fragments.buysim.otpverification.pojo.OtpVerifyResponse;
import com.sb.woohoo.ui.main.fragments.buysim.phonenumber.pojo.GetAllPhoneNumbersResponse;
import com.sb.woohoo.ui.main.fragments.overview.saudidestinations.pojo.SaudiTourismDestinationsResponse;
import com.sb.woohoo.ui.main.fragments.overview.saudipackages.pojo.SaudiTourismPackagesResponse;
import com.google.gson.JsonObject;
import com.sb.woohoo.ui.main.pojo.agentic.AgenticAckRequest;
import com.sb.woohoo.ui.main.pojo.agentic.AgenticTurnRequest;
import com.sb.woohoo.ui.main.pojo.agentic.AgenticTurnResponse;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.GetAlarm;
import com.sb.woohoo.ui.main.fragments.alarm.pojo.PostAlarm;
import com.sb.woohoo.ui.main.fragments.amadeusServices.pojo.AmadeusDynamicOrderRequest;
import com.sb.woohoo.ui.main.fragments.amadeusServices.pojo.AmadeusOrdersResponse;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.ContactsResponse;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMembersResponse;
import com.sb.woohoo.ui.main.fragments.contactsprofile.pojo.StaffMemberResponse;
import com.sb.woohoo.ui.main.fragments.covid.pojo.CovidSubmitRequest;
import com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.PhilipsHueDevice;
import com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.PhilipsHueDeviceResponse;
import com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.PhilipsHueLightContainer;
import com.sb.woohoo.ui.main.fragments.devices.discovery.pojo.register.PostLightData;
import com.sb.woohoo.ui.main.fragments.devices.huecontrol.pojo.LightStateChangeResponse;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.GetAllDevices;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.getlight.Light;
import com.sb.woohoo.ui.main.fragments.devices.launcher.pojo.lightinfo.LightFullInfo;
import com.sb.woohoo.ui.main.fragments.diningrestaurants.pojo.DiningRestaurantResponse;
import com.sb.woohoo.ui.main.fragments.faq.categories.pojo.FaqCategoriesResponse;
import com.sb.woohoo.ui.main.fragments.faq.questions.pojo.FaqQuestionsResponse;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.FeedbackSubmitRequest;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.QuestionRequest;
import com.sb.woohoo.ui.main.fragments.feedback.pojo.QuestionSendRequest;
import com.sb.woohoo.ui.main.fragments.fnb.categories.pojo.FnBCategoriesResponse;
import com.sb.woohoo.ui.main.fragments.fnb.history.pojo.FnBHistoryResponse;
import com.sb.woohoo.ui.main.fragments.fnb.order.pojo.PlaceOrderRequest;
import com.sb.woohoo.ui.main.fragments.fnb.order.pojo.PlaceOrderResponse;
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.FnBProductsResponse;
import com.sb.woohoo.ui.main.fragments.fnb.sub_categories.di.pojo.FnBSubCategoriesResponse;
import com.sb.woohoo.ui.main.fragments.home.pojo.HotelDetailsResponse;
import com.sb.woohoo.ui.main.fragments.interel.pojo.DynamicUtilityResponse;
import com.sb.woohoo.ui.main.fragments.interel.pojo.InterelUtilityResponse;
import com.sb.woohoo.ui.main.fragments.interel.pojo.UpdateDynamicTypeUtilityRequest;
import com.sb.woohoo.ui.main.fragments.interel.pojo.UpdateDynamicUtilityRequest;
import com.sb.woohoo.ui.main.fragments.interel.pojo.UtilityUpdationResponse;
import com.sb.woohoo.ui.main.fragments.livefeed.pojo.LiveFeedTokenModel;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.HubListModel;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.HubStatusModel;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.OtherFamilyModel;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.OthersHubListModel;
import com.sb.woohoo.ui.main.fragments.livefeedowndevice.pojo.StartLiveFeedModel;
import com.sb.woohoo.ui.main.fragments.music.pojo.Parameter;
import com.sb.woohoo.ui.main.fragments.music.pojo.song.Song;
import com.sb.woohoo.ui.main.fragments.music.pojo.token.AccessToken;
import com.sb.woohoo.ui.main.fragments.newsaudio.pojo.NewsAudioResponse;
import com.sb.woohoo.ui.main.fragments.newstext.headline.pojo.NewsHeadline;
import com.sb.woohoo.ui.main.fragments.overview.categories.pojo.OverviewCategoriesResponse;
import com.sb.woohoo.ui.main.fragments.overview.item.pojo.OverviewItemResponse;
import com.sb.woohoo.ui.main.fragments.overview.saudievents.pojo.SaudiTourismResponse;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperience.pojo.SaudiTourismExperienceResponse;
import com.sb.woohoo.ui.main.fragments.overview.saudiexperiencedetail.pojo.SaudiTourismExperienceDetailsResponse;
import com.sb.woohoo.ui.main.fragments.overview.subcategories.pojo.OverviewSubCategoriesResponse;
import com.sb.woohoo.ui.main.fragments.qna.pojo.WolframResponse;
import com.sb.woohoo.ui.main.fragments.qrcode.pojo.QRCodeResponse;
import com.sb.woohoo.ui.main.fragments.relaxationmusic.pojo.RelaxationMusicCategoriesResponse;
import com.sb.woohoo.ui.main.fragments.relaxationmusic.pojo.RelaxationMusicTracksResponse;
import com.sb.woohoo.ui.main.fragments.reminders.pojo.Reminder;
import com.sb.woohoo.ui.main.fragments.reminders.pojo.ReminderGet;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteActionResponse;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteChannelRequest;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteChannelResponse;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteRequest;
import com.sb.woohoo.ui.main.fragments.remote.pojo.TVRemoteResponse;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.DiningRestaurantRequest;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.DiningRestaurantTimeSlotResponse;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.DiningRestaurantsDateResponse;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.ReservationRequest;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.ReservationServicesResponse;
import com.sb.woohoo.ui.main.fragments.reservation.booking.pojo.ReservationTimeSlotsResponse;
import com.sb.woohoo.ui.main.fragments.reservation.confrim.pojo.BookingConfirmResponse;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.CancelBookingResponse;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.UpcomingReservationResponse;
import com.sb.woohoo.ui.main.fragments.reservation.upcoming.pojo.UpcomingServicesResponse;
import com.sb.woohoo.ui.main.fragments.screensaver.pojo.ScreenSaverMultiResponse;
import com.sb.woohoo.ui.main.fragments.settings.devicesettings.pojo.DeviceSettings;
import com.sb.woohoo.ui.main.fragments.settings.devicesettings.pojo.TimeZoneUpdate;
import com.sb.woohoo.ui.main.fragments.settings.language.pojo.LanguageResponse;
import com.sb.woohoo.ui.main.fragments.settings.thingstosay.pojo.ThingsToSayResponse;
import com.sb.woohoo.ui.main.fragments.settings.update.UpdateModel;
import com.sb.woohoo.ui.main.fragments.surveillancemode.pojo.UserDetailModel;
import com.sb.woohoo.ui.main.fragments.surveillancemode.surveillanceinitial.pojo.SurveillanceModeResponse;
import com.sb.woohoo.ui.main.fragments.todo.pojo.ToDoData;
import com.sb.woohoo.ui.main.fragments.todo.pojo.ToDoResponse;
import com.sb.woohoo.ui.main.fragments.video.pojo.VideoResponse;
import com.sb.woohoo.ui.main.fragments.video.pojo.VideoTrendingResponse;
import com.sb.woohoo.ui.main.fragments.weather.di.LatLngByIPModel;
import com.sb.woohoo.ui.main.fragments.weather.pojo.WeatherRequest;
import com.sb.woohoo.ui.main.fragments.weather.pojo.response.Weather;

import com.sb.woohoo.ui.main.pojo.OrderDetailsResponse;

import com.sb.woohoo.ui.main.pojo.Appstore;
import com.sb.woohoo.ui.main.pojo.EndSession;

import com.sb.woohoo.ui.main.pojo.PostQuery;
import com.sb.woohoo.ui.main.pojo.StartSession;
import com.sb.woohoo.ui.main.pojo.StatsDumpRequest;
import com.sb.woohoo.ui.main.pojo.StatsDumpResponse;
import com.sb.woohoo.ui.main.pojo.nlp.NLPResponse;
import com.sb.woohoo.ui.main.pojo.sip.SIPConfigurationResponse;
import com.sb.woohoo.ui.main.pojo.translate.PostTranslateQuery;
import com.sb.woohoo.ui.main.pojo.translate.TranslateResponse;
import com.sb.woohoo.ui.onboarding.login.LoginModel;
import com.sb.woohoo.ui.onboarding.pojo.RegisterHubParameters;
import com.sb.woohoo.ui.onboarding.pojo.RegisterHubResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by admin on 1/31/18.
 */

public interface WooHooApiCall {

    /*--------------------------------------------------- API : Login,LogOut & User Data ---------------------------------------------------------*/

    @FormUrlEncoded
    @POST("user/login")
    @Headers("No-Authentication: true")
    Single<LoginModel> getLoginResponse(@Field("userName") String userName,
                                        @Field("password") String password,
                                        @Field("deviceId") String deviceID);

    @POST("user/logout")
    Completable logOutUser();

    @POST("hub")
    Single<RegisterHubResponse> registerHubDetails(
            @Body RegisterHubParameters parameters
    );

    @GET("user")
    Single<UserDetailModel> getUserDetails(@Query("id") String userId);

    /*--------------------------------------------------- API : Reminders ---------------------------------------------------------*/

    @GET("reminder/{id}")
    Single<ReminderGet> getReminders(@Path("id") String hubId);

    @FormUrlEncoded
    @POST("reminder")
    Single<Reminder> postReminder(
            @Field("reminder") String reminder,
            @Field("dateTime") String date_time,
            @Field("description") String description,
            @Field("hubId") String hubId
    );

    @DELETE("reminder/{id}")
    Completable deleteReminder(
            @Path("id") String reminderId
    );

    @DELETE("reminder/all/{hubId}")
    Completable deleteReminders(@Path("hubId") String hubId);


    @FormUrlEncoded
    @POST("reminder/{id}")
    Completable putReminder(
            @Path("id") String id,
            @Field("reminder") String reminder,
            @Field("dateTime") String date_time,
            @Field("description") String description,
            @Field("hubId") String hubId);

    /*--------------------------------------------------- API : Hubs ---------------------------------------------------------*/

    @GET("user/hubs")
    Single<HubListModel> getAllHubs();

    @GET("family/otherFamilies")
    Single<OtherFamilyModel> getHubAccessFromOtherFamily();

    @GET("family/otherFamilyMember/hubs/{id}")
    Single<OthersHubListModel> getHubListOfOtherFamilyMembers(@Path("id") String id);

    @GET("contact/hubAccess/{contactId}")
    Single<HubListModel> getAccessibleHubsOfContacts(@Path("contactId") String contactId);

    /*--------------------------------------------------- API : To Do ---------------------------------------------------------*/

    @GET("todo")
    Single<ToDoResponse> getToDos();

    @FormUrlEncoded
    @POST("todo")
    Single<ToDoData> addNewToDo(@Field("name") String todoName,
                                @Field("hubId") String hubId);

    @DELETE("todo")
    Completable deleteAllToDos();

    @DELETE("todo/{id}")
    Completable deleteASpecificToDo(@Path("id") String todoID);

    /*------------------------------------------------- API : Contacts & Family --------------------------------------------------------*/

    @GET("contact")
    Single<ContactsResponse> getAllContacts();

    @FormUrlEncoded
    @POST("propertyContact/name")
    Single<StaffMemberResponse> getContactByName(@Field("deviceId") String deviceId, @Field("name") String name);

    @FormUrlEncoded
    @POST("propertyContact/device")
    Single<StaffMembersResponse> getAllHotelStaffMembers(@Field("deviceId") String deviceId);

    /*---------------------------------------------------API: Live Feed & Surveillance------------------------------------------------*/

    @GET("hub/{hubID}/status")
    Single<HubStatusModel> getHubStatus(@Path("hubID") String hubID);

    @GET("hub/{hubID}/{userId}/status")
    Single<HubStatusModel> getHubUserStatus(@Path("hubID") String hubID, @Path("userId") String userId);

    @FormUrlEncoded
    @PUT("hub/{id}/status")
    Completable updateHubStatus(@Path("id") String id,
                                @Field("isBusy") Boolean isBusy);

    @FormUrlEncoded
    @POST("liveFeed/token")
    Single<LiveFeedTokenModel> getLiveFeedToken(@Field("hubId") String deviceId);

    @FormUrlEncoded
    @POST("liveFeed/{hub_id}")
    Single<StartLiveFeedModel> startLiveFeed(@Path("hub_id") String hubID,
                                             @Field("senderHubId") String senderHubId,
                                             @Field("contactLiveFeed") boolean contactLiveFeed,
                                             @Field("contactUserId") String userId,
                                             @Field("feedType") String feedType);

    @FormUrlEncoded
    @PUT("hub/surveillance/{hub_Id}")
    Single<SurveillanceModeResponse> changeHubModeToSurveillance(@Path("hub_Id") String hub_id,
                                                                 @Field("surveillanceMode") boolean isInSurveillanceMode,
                                                                 @Field("pin") String pin,
                                                                 @Field("from") String device);

    @FormUrlEncoded
    @PUT("user/pin")
    Completable setUserPin(@Field("pin") String pin);

    /*------------------------------------------------ API : QNA --------------------------------------------------------*/

    @FormUrlEncoded
    @POST("wolfram")
    Single<WolframResponse> requestQNA(@Field("query") String query);

    /*------------------------------------------------ API : DialogFlow --------------------------------------------------------*/

    @POST("flow/conversation")
    Single<NLPResponse> languageProcessRequest(@Body PostQuery postQuery);

    @POST
    Single<NLPResponse> languageProcessRequestLocal(@Url String url, @Body PostQuery postQuery);

    @POST("flow/conversation")
    Single<NLPResponse> languageSmartFlow(@Body PostQuery postQuery);

    @POST
    Single<NLPResponse> languageSmartFlowLocal(@Url String url, @Body PostQuery postQuery);

    @POST
    Single<NLPResponse> geminiIntentRequest(@Url String url, @Body PostQuery postQuery);

    /*------------------------------------------------ API : Agentic platform ------------------------------------------------*/

    /**
     * One conversational turn against the agentic gateway.
     *
     * The response carries speech plus two arrays that must not be confused: `calls` is work
     * already executed server-side, `deviceCommands` is work for this device. See
     * {@link AgenticTurnResponse}.
     *
     * `pilotKey` is not device identity and must not be read as it. Device identity is a
     * verified client certificate, which a TLS terminator turns into a header the gateway
     * trusts; a key compiled into an APK is extractable by anyone holding the APK. Its one
     * job is to stop a gateway on a public IP from answering port scanners, which matters
     * because device auth cannot be enforced until certificates are provisioned into the
     * hubs and /v1/turn otherwise accepts anyone who knows a deviceId. Pass the empty
     * string against a gateway that does not ask for it; an unexpected header is ignored.
     */
    @POST
    Single<AgenticTurnResponse> agenticTurn(@Url String url,
                                            @Header("x-pilot-key") String pilotKey,
                                            @Body AgenticTurnRequest request);

    /**
     * Report what this device did with a dispatched command.
     *
     * Fire and forget. The guest was answered long before this is sent, and its only purpose
     * is to make a device that silently failed visible to operations.
     */
    @POST
    Completable agenticCommandAck(@Url String url,
                                  @Header("x-pilot-key") String pilotKey,
                                  @Body AgenticAckRequest request);

    @FormUrlEncoded
    @POST("dialogFlow/reset")
    Completable resetDialogFlowContext(@Field("sessionId") String sessionId);

    @POST("https://nlp.infer.smartbeings.ai/nlpapi/conversation/translate")
    Single<TranslateResponse> translate(@Body PostTranslateQuery postQuery);


    /*------------------------------------------------ API : Youtube -----------------------------------------------------------*/

    @FormUrlEncoded
    @POST("youtube/search")
    Single<VideoResponse> getMediaList(@Field("query") String query);

    @FormUrlEncoded
    @POST("youtube/search")
    Single<VideoTrendingResponse> getTrendingList(@Field("query") String query, @Field("countryCode") String countryCode);

    /*------------------------------------------------ API : Spotify -----------------------------------------------------------*/

    @POST("spotify/search")
    Single<Song> getSongs(@Body Parameter parameter);

    @GET("spotify/topCharts")
    Single<Song> getSongs();

    @GET("spotify")
    Single<AccessToken> getToken();

    /*----------------------------------------- API : Relaxation Music --------------------------------------------------*/

    @GET("relaxationMusic/default/categories")
    Single<RelaxationMusicCategoriesResponse> getDefaultRelaxationMusicCategories();

    @GET("relaxationMusic/default/tracks")
    Single<RelaxationMusicTracksResponse> getDefaultRelaxationMusicTracks();

    @GET("relaxationMusic/hotels/{propertyId}/categories")
    Single<RelaxationMusicCategoriesResponse> getHotelRelaxationMusicCategories(@Path("propertyId") int propertyId);

    @GET("relaxationMusic/hotels/{propertyId}/tracks")
    Single<RelaxationMusicTracksResponse> getHotelRelaxationMusicTracks(@Path("propertyId") int propertyId);

    /*----------------------------------------------- API : News --------------------------------------------------------*/

    @GET("news/location/{location}")
    Single<NewsHeadline> getNewsLocationApiRequest(@Path("location") String location);

    @GET("news/general/{query}")
    Single<NewsHeadline> getNewsQueryApiRequest(@Path("query") String query);

    @GET("news/npr")
    Single<NewsAudioResponse> getNewsBroadcastAudio();

    /*----------------------------------------------- API : Feedback --------------------------------------------------------*/
    @FormUrlEncoded
    @POST("feedback/getQuestions")
    Single<QuestionRequest> getFeedbackQuestionsRequest(@Field ("deviceId") String deviceId);

    @POST("feedback/save")
    Single<QuestionSendRequest> postFeedbackQuestions(@Body FeedbackSubmitRequest feedbackSubmitRequest);

    /*----------------------------------------------- API : Covid --------------------------------------------------------*/
    @FormUrlEncoded
    @POST("covid/getQuestions")
    Single<com.sb.woohoo.ui.main.fragments.covid.pojo.QuestionRequest> getCovidQuestionsRequest(@Field ("deviceId") String deviceId);

    @POST("covid/save")
    Single<com.sb.woohoo.ui.main.fragments.covid.pojo.QuestionSendRequest> postCovidQuestions(@Body CovidSubmitRequest feedbackSubmitRequest);

    /*----------------------------------------------- API : QR Code --------------------------------------------------------*/

    @GET("fnb/qrcode")
    Single<QRCodeResponse> getQRCode();

    /*----------------------------------------------- API : F&B --------------------------------------------------------*/

    @GET("fnb/categories")
    Single<FnBCategoriesResponse> getFnBCategories();

    @GET("fnb/subCategories/{id}")
    Single<FnBSubCategoriesResponse> getFnBSubCategories(@Path("id") String id);

    @GET("fnb/products")
    Single<FnBProductsResponse> getFnBProducts(@Query("categoryId") String id);

    @GET("fnb/products")
    Single<FnBProductsResponse> getFnBProductsFromSubCategoryId(@Query("subCategoryId") String id);

    @POST("fnbOrder/order")
    Single<PlaceOrderResponse> placeOrder(@Body PlaceOrderRequest placeOrderRequest);

    @GET("fnbOrder/orders")
    Single<FnBHistoryResponse> getFnBOrdersHistory();

    /*------------------------------------------------- API : Reservation ---------------------------------------------------------*/

    @GET("propertyServices/device")
    Single<ReservationServicesResponse> getReservationServices();

    @GET("propertyServices/slots/{id}/{date}")
    Single<ReservationTimeSlotsResponse> getReservationTimeSlot(@Path("id") String id, @Path("date") String date);

    @POST("propertyServices/booking")
    Single<BookingConfirmResponse> makeReservation(@Body ReservationRequest reservationRequest);

    @GET("propertyServices/bookings")
    Single<UpcomingServicesResponse> getUpcomingServices();

    @DELETE("propertyServices/booking")
    Single<CancelBookingResponse> makeCancelBookingRequest(@Query("id") String id);

    /*------------------------------------------------- API : statsDump -------------------------------------------------------------*/

    @POST("flow/statsDump")
    Single<StatsDumpResponse> callStatsDumpAPI(@Body StatsDumpRequest statsDumpRequest);

    /*------------------------------------------------- API : Weather -------------------------------------------------------------*/

    @POST("weather")
    Single<Weather> getWeatherUpdate(@Body WeatherRequest weatherRequest);

    @FormUrlEncoded
    @PUT("user/getLoc/{ip}")
    Single<LatLngByIPModel> getLatlngWithIP(@Path("ip") String ip,@Field("deviceId") String deviceId);

    /*------------------------------------------------- API : Setting -------------------------------------------------------------*/

    @GET("hub")
    Single<DeviceSettings> getDeviceSetting(@Query("id") String id);

    @GET("thingsToSay")
    Single<ThingsToSayResponse> getThingsToSay();

    @FormUrlEncoded
    @PUT("hub/{id}")
    Completable updateHubName(@Path("id") String id,
                              @Field("name") String name);

//    @GET("version")
//    Single<UpdateModel> getUpdate();

    @FormUrlEncoded
    @POST("version/domain")
    Single<UpdateModel> getUpdate(@Field("deviceId") String deviceId);


    @FormUrlEncoded
    @PUT("hub/{id}")
    Completable updateAppVersion(@Path("id") String id,
                              @Field("appVersion") String version);

    /*------------------------------------------------- API : Appstore -------------------------------------------------------------*/

    @FormUrlEncoded
    @POST("appStore/all")
    Single<Appstore> getAppStoreList(@Field("deviceId") String deviceId);

    @FormUrlEncoded
    @POST("appStore/startSession")
    Single<StartSession> startSession(@Field("deviceId") String deviceId,@Field("appStoreId") String appStoreId);

    @FormUrlEncoded
    @POST("appStore/endSession")
    Single<EndSession> endSession(@Field("sessionId") String sessionId);

    /*--------------------------------------------------- API : Alarm -----------------------------------------------------------*/

    @GET("alarm/{hubId}")
    Single<GetAlarm> getAlarms(@Path("hubId") String hubId);

    @FormUrlEncoded
    @POST("alarm")
    Single<PostAlarm> postAlarm(@Field("hubId") String hubId,
                                @Field("dateTime") String time,
                                @Field("isEveryDayAlarm") boolean isEveryday
    );

    @DELETE("alarm/all/{hubId}")
    Completable deleteAllAlarms(@Path("hubId") String hubId);

    @DELETE("alarm/{id}")
    Completable deleteAlarm(@Path("id") String alarmId);

    @FormUrlEncoded
    @POST("alarm/{id}")
    Completable updateAlarm(@Path("id") String alarmId,
                            @Field("dateTime") String time,
                            @Field("isEveryDayAlarm") boolean isEveryday,
                            @Field("hubId") String hubId);

    /*--------------------------------------------------- API : Hue Devices -----------------------------------------------------------*/

    @GET
    Single<List<PhilipsHueDevice>> searchBridges(@Url String url);

    @POST
    Single<List<PhilipsHueDeviceResponse>> authorizationDevice(@Url String url,
                                                               @Body JsonObject jsonObject);

    @GET
    Single<PhilipsHueLightContainer> getDevicesFromHub(@Url String url);

    @GET
    Single<LightFullInfo> getLightStatus(@Url String url);

    @POST
    Completable searchLightFromHub(@Url String url);

    @GET
    Completable getNewLightFromHub(@Url String url);

    @DELETE
    Completable deleteLightFromHub(@Url String url);

    @PUT
    Completable renameLightFromHub(@Url String url,  @Body JsonObject jsonObject);

    @PUT
    Single<List<LightStateChangeResponse>> changeLightState(@Url String url,
                                                            @Body JsonObject jsonObject);

    @POST("smartHomeDevice")
    Completable registerPhilipsHub(@Body PostLightData postLightData);

    @GET("smartHomeDevice")
    Single<GetAllDevices> getDevicesList();

    @PUT("smartHomeDevice/{id}")
    Completable updateLightName(@Path("id") String lightId,
                                @Body JsonObject jsonObject);

    @DELETE("smartHomeDevice/{id}")
    Completable deleteLight(@Path("id") String lightId);

    @PUT("smartHomeDevice/state/{id}")
    Completable updateLightData(@Path("id") String id,
                                @Body Light light);

    @GET("interel/getRoomStatus/{hubId}")
    Single<InterelUtilityResponse> getRoomState(@Path("hubId") String hubId);

    @FormUrlEncoded
    @POST("interel/setDnd")
    Single<UtilityUpdationResponse> setDNDMode(@Field("data") String status,
                                               @Field("deviceId") String hubId);

    @FormUrlEncoded
    @POST("interel/setMur")
    Single<UtilityUpdationResponse> setMurMode(@Field("data") String status,
                                               @Field("deviceId") String hubId);

    @FormUrlEncoded
    @POST("interel/callScene")
    Single<UtilityUpdationResponse> setCurtainState(@Field("data") String state,
                                                    @Field("deviceId") String hubId);

    @FormUrlEncoded
    @POST("interel/setBlindState")
    Single<UtilityUpdationResponse> setBlindState(@Field("data") String state,
                                                  @Field("deviceId") String hubId);

    @FormUrlEncoded
    @POST("interel/callScene")
    Single<UtilityUpdationResponse> setMasterOnOff(@Field("data") String state,
                                                   @Field("deviceId") String hubId);

    @FormUrlEncoded
    @POST("interel/callScene")
    Single<UtilityUpdationResponse> setToggleLight(@Field("data") String state,
                                                   @Field("deviceId") String hubId);


    @FormUrlEncoded
    @POST("interel/changeSetpoint")
    Single<UtilityUpdationResponse> changeThermostatSetPoint(@Field("data") Integer tempPoint,
                                                             @Field("deviceId") String hubId);

    @FormUrlEncoded
    @POST("interel/setFanMode")
    Single<UtilityUpdationResponse> changeFanMode(@Field("data") String fanMode,
                                                  @Field("deviceId") String deviceId);

    /********************************************@Amadeus_Services************************************/

    @GET("housekeeping/items")
    Single<AmadeusOrdersResponse> getAllDynamicOrders();

    @POST("housekeeping/createOrder")
    Single<UtilityUpdationResponse> createAmadeusDynamicOrder(@Body AmadeusDynamicOrderRequest amadeusDynamicOrderRequest);

    @FormUrlEncoded
    @POST("properties/device")
    Single<HotelDetailsResponse> getHotelDetails(@Field("deviceId") String deviceId);

    @PUT("hub/timeZone/{hubId}")
    Single<TimeZoneUpdate> updateTimeZone(@Path("hubId") String hubId,
                                          @Body JsonObject jsonObject);

    /*************************************************{@GET_HOTEL_DETAILS}*****************************/

    @FormUrlEncoded
    @POST("properties/screensaver/device")
    Single<ScreenSaverMultiResponse> getScreenSaverMulti(@Field("deviceId") String deviceId);

    @FormUrlEncoded
    @POST("alert/order")
    Single<OrderDetailsResponse> placeOrder(@Field("hubId") String hubId,
                                            @Field("quantity") String quantity,
                                            @Field("deviceId") String deviceId,
                                            @Field("price") String price,
                                            @Field("userId") String userId,
                                            @Field("itemName") String itemName);


    /*****************  SIP APIS REQUEST  ************************/

    @GET("/v1/locale")
    Single<LanguageResponse> getAllLocale();


    /*****************  SIP APIS REQUEST  ************************/

    @GET("/v1/sip-configure/room/credentials")
    Single<SIPConfigurationResponse> getSIPConfiguration();


    /*****************  Saudi Tourism APIS REQUEST  ************************/

    @GET("/v1/tourism/saudi/events")
    Single<SaudiTourismResponse> getEventsForSaudiTourism(@Query("locale") String lang,@Query("limit") String limit);

    @GET("/v1/tourism/saudi/experience")
    Single<SaudiTourismExperienceResponse> getExperienceForSaudiTourism(@Query("locale") String lang,@Query("page") String limit);

    @GET("/v1/tourism/saudi/experience/{id}")
    Single<SaudiTourismExperienceDetailsResponse> getExperienceIDForSaudiTourism(@Path("id") String id,@Query("locale") String lang);

    @GET("/v1/tourism/saudi/packages?locale=en&limit=50")
    Single<SaudiTourismPackagesResponse> getPackagesForSaudiTourism();

    @GET("/v1/tourism/saudi/destinations")
    Single<SaudiTourismDestinationsResponse> getDestinationsForSaudiTourism(@Query("locale") String lang,@Query("limit") String limit);

    /***************** TV Remote **********************/
    @POST("tvcontrol/control")
    Single<TVRemoteChannelResponse> getAllChannel(@Body TVRemoteRequest tVRemoteRequest);

    @GET("tvcontrol/actions")
    Single<TVRemoteResponse> getAllTVActions();

    @POST("tvcontrol/control")
    Single<TVRemoteActionResponse> controlTVCommand(@Body TVRemoteRequest tVRemoteRequest);

    @POST("tvcontrol/control")
    Single<TVRemoteActionResponse> controlChannelTVCommand(@Body TVRemoteChannelRequest tVRemoteRequest);

    /***************** TV Remote **********************/
    @GET("/v1/telecom/packages")
    Single<GetAllPackagesResponse> getAllPackages();

    @GET("/v1/telecom/mobile-numbers")
    Single<GetAllPhoneNumbersResponse> getAllPhoneNumber();

    @POST("/v1/telecom/save-email")
    Single<OtpSendResponse> sendOtpToEmailId(@Body EmailIdRequest request);

    @POST("/v1/telecom/verify-email")
    Single<OtpVerifyResponse> otpVerify(@Body EmailIdOtpRequest request);

    @POST("/v1/telecom")
    Single<BuyPackageResponse> callBuyPackageApi(@Body BuyPackageRequest request);

    /*****************  FAQ APIS REQUEST  ************************/
        @GET("faq/categories")
    Single<FaqCategoriesResponse> getFAQCategories();

    @GET("faq/category/{categoryID}")
    Single<FaqQuestionsResponse> getAllQuestionsOfCategory(@Path("categoryID") String categoryID);



    @GET("aboutUs/categories")
    Single<OverviewCategoriesResponse> getOverviewCategories();

    @GET("aboutUs/subCategories/{categoryId}")
    Single<OverviewSubCategoriesResponse> getOverviewSubCategories(@Path("categoryId") String id);

    @GET("aboutUs/items")
    Single<OverviewItemResponse> getOverviewProducts(@Query("categoryId") String id);


    @GET("restaurants")
    Single<DiningRestaurantResponse> getRestaurants();

    @GET("houseKeeping/items")
    Single<DynamicUtilityResponse> getInterelDynamicUtilities(@Query("type") String type);

    @POST("housekeeping/createOrder")
    Single<UtilityUpdationResponse> updateDynamicUtility(@Body UpdateDynamicUtilityRequest updateDynamicUtilityRequest);

    @POST("housekeeping/createOrder")
    Single<UtilityUpdationResponse> updateDynamicUtilityType(@Body UpdateDynamicTypeUtilityRequest updateDynamicTypeUtilityRequest);

    @GET("tablecheck/dates/{id}")
    Single<DiningRestaurantsDateResponse> getDiningReservationDates(@Path("id") String id);

    @GET("tablecheck/slots/{id}/{date}")
    Single<DiningRestaurantTimeSlotResponse> getDiningReservationDates(@Path("id") String id, @Path("date") String date);

    @POST("tablecheck")
    Single<ResponseBody> makeDiningRestaurantBooking(@Body DiningRestaurantRequest restaurantRequest);

    @GET("tablecheck/reservations/{id}")
    Single<UpcomingReservationResponse> getAllDiningReservations(@Path("id") String id);
}
