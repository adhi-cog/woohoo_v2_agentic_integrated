package com.sb.woohoo.analytics

import android.content.Context
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.sb.woohoo.ApplicationContext
import com.sb.woohoo.data.prefs.SharedPrefsHelper
import com.sb.woohoo.data.prefs.SharedPrefsKey
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Product
import javax.inject.Inject


class AnalyticsHelper @Inject constructor(@ApplicationContext context: Context, prefsHelper: SharedPrefsHelper){

    private val isGmsSupported: Boolean
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private var mPrefsHelper: SharedPrefsHelper? = prefsHelper

    init {
        isGmsSupported = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        if(isGmsSupported) {
            FirebaseApp.initializeApp(context)
            firebaseAnalytics = FirebaseAnalytics.getInstance(context).apply {
                setUserId(mPrefsHelper?.get(SharedPrefsKey.PREF_KEY_USER_ID, ""))
                setUserProperty(UserProperty.LOGIN_ID, mPrefsHelper?.get(SharedPrefsKey.PREF_KEY_USER_ID, ""))
                setUserProperty(UserProperty.DEVICE_ID, mPrefsHelper?.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, ""))
                setUserProperty(UserProperty.DOMAIN_ID, mPrefsHelper?.get(SharedPrefsKey.PREF_DOMAIN_ID, ""))
            }
        }
    }

    fun updateHelper() {
        if(isGmsSupported) {
            firebaseAnalytics?.apply {
                setUserId(mPrefsHelper?.get(SharedPrefsKey.PREF_KEY_USER_ID, ""))
                setUserProperty(UserProperty.LOGIN_ID, mPrefsHelper?.get(SharedPrefsKey.PREF_KEY_USER_ID, ""))
                setUserProperty(UserProperty.DEVICE_ID, mPrefsHelper?.get(SharedPrefsKey.PREF_KEY_DEVICE_ID, ""))
                setUserProperty(UserProperty.DOMAIN_ID, mPrefsHelper?.get(SharedPrefsKey.PREF_DOMAIN_ID, ""))
            }
        }
    }


    fun track(screenName: Screen, attributes: Map<String, String>? = emptyMap()) {
        if(isGmsSupported) {
            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
                this.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName.name)
            })
        }
    }

    fun track(log: String, attributes: Bundle? = null){
        if(isGmsSupported) {
            firebaseAnalytics?.logEvent(log, attributes?:Bundle())
        }
    }

    fun trackEcommerce(event: EcommerceEvent) {
        if(isGmsSupported) {

            //TODO - Add more ecommerce events handling
            when (event) {
                is EcommerceEvent.ViewItemList ->
                    firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, Bundle().apply {
                        this.putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, event.itemListName)
                        this.putString(FirebaseAnalytics.Param.ITEMS, ArrayList(event.items).toString())
                    })

                is EcommerceEvent.SelectItem ->
                    firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, Bundle().apply {
                        this.putString(FirebaseAnalytics.Param.CURRENCY, event.CURRENCY)
                        this.putDouble(FirebaseAnalytics.Param.VALUE, event.itemPrice)
                        this.putString(FirebaseAnalytics.Param.ITEM_ID, event.itemId)
                        this.putString(FirebaseAnalytics.Param.ITEM_NAME, event.itemName)
                        this.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, event.category)
                        this.putString(FirebaseAnalytics.Param.ITEM_CATEGORY2, event.subCategory)
                    })

                is EcommerceEvent.AddToCart ->
                    firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, Bundle().apply {
                        this.putString(FirebaseAnalytics.Param.CURRENCY, event.CURRENCY)
                        this.putString(FirebaseAnalytics.Param.ITEM_ID, event.itemId)
                        this.putDouble(FirebaseAnalytics.Param.VALUE, event.itemPrice)
                        this.putString(FirebaseAnalytics.Param.ITEM_NAME, event.itemName)
                    })

                is EcommerceEvent.BeginCheckout ->
                    firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, Bundle().apply {
                        this.putString(FirebaseAnalytics.Param.CURRENCY, event.CURRENCY)
                        this.putDouble(FirebaseAnalytics.Param.VALUE, event.price)
                        this.putString(FirebaseAnalytics.Param.ITEMS, ArrayList(event.items).toString())
                    })

                is EcommerceEvent.Purchase ->
                    firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.PURCHASE, Bundle().apply {
                        this.putString(FirebaseAnalytics.Param.CURRENCY, event.CURRENCY)
                        this.putString(FirebaseAnalytics.Param.ITEMS, ArrayList(event.items).toString())
                        this.putString(FirebaseAnalytics.Param.TRANSACTION_ID, event.transactionId)
                        this.putDouble(FirebaseAnalytics.Param.VALUE, event.price)
                    })

                is EcommerceEvent.RemoveFromCart ->
                    firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART, Bundle().apply {
                        this.putString(FirebaseAnalytics.Param.CURRENCY, event.CURRENCY)
                        this.putString(FirebaseAnalytics.Param.ITEM_ID, event.itemId)
                        this.putDouble(FirebaseAnalytics.Param.VALUE, event.itemPrice)
                        this.putString(FirebaseAnalytics.Param.ITEM_NAME, event.itemName)
                    })

                is EcommerceEvent.SelectPromotion ->
                    firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_PROMOTION, Bundle().apply {
                        this.putString(FirebaseAnalytics.Param.ITEMS, ArrayList(event.items).toString())
                        this.putString(FirebaseAnalytics.Param.PROMOTION_ID, event.promotionId)
                        this.putString(FirebaseAnalytics.Param.PROMOTION_NAME, event.promotionName)
                        this.putString(FirebaseAnalytics.Param.CREATIVE_NAME, event.creativeName)
                        this.putString(FirebaseAnalytics.Param.CREATIVE_SLOT, event.creativeSlot)
                        this.putString(FirebaseAnalytics.Param.LOCATION_ID, event.locationId)
                    })

                is EcommerceEvent.ViewPromotion ->
                    firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.VIEW_PROMOTION, Bundle().apply {
                        this.putString(FirebaseAnalytics.Param.PROMOTION_ID, event.promotionId)
                        this.putString(FirebaseAnalytics.Param.PROMOTION_NAME, event.promotionName)
                        this.putString(FirebaseAnalytics.Param.CREATIVE_NAME, event.creativeName)
                        this.putString(FirebaseAnalytics.Param.CREATIVE_SLOT, event.creativeSlot)
                        this.putString(FirebaseAnalytics.Param.LOCATION_ID, event.locationId)
                    })
            }

        }
    }


    fun bookItem(event: TourismEvent) {
        if(isGmsSupported) {

            //TODO - Add more ecommerce events handling
            when (event) {
                is TourismEvent.SelectItem ->
                    firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, Bundle().apply {
                        this.putString(FirebaseAnalytics.Param.ITEM_NAME, event.itemName)
                        this.putString(FirebaseAnalytics.Param.ITEM_ID, event.itemId)
                    })
            }

        }
    }

    /*  fun trackFnb(event: FnbEvent)
      {
          if(isGmsSupported)
          {
              when (event)
              {
                  is FnbEvent.FnbCategoriesInitiated ->
                      firebaseAnalytics?.logEvent(FNB_CATEGORIES_INITIATED, null)

                  is FnbEvent.FnbCategoriesSuccess ->
                      firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
                          this.putString(FirebaseAnalytics.Param.SCREEN_NAME, FNB_CATEGORIES_SUCCESS)
                      })

                  is FnbEvent.FnbCategoriesError ->
                      firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
                          this.putString(FirebaseAnalytics.Param.SCREEN_NAME, FNB_CATEGORIES_ERROR)
                          this.putString(FirebaseAnalytics.Param.VALUE, event.error)
                      })

                  is FnbEvent.FnbCategoriesPresented ->
                      firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
                          this.putString(FirebaseAnalytics.Param.SCREEN_NAME, FNB_CATEGORIES_PRESENTED)
                      })

                  is FnbEvent.FnbCategoriesSelected ->
                      firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, event.bundle.also {
                          it.putString(FirebaseAnalytics.Param.SCREEN_NAME, FNB_CATEGORIES_SELECTED)
                      })

                  is FnbEvent.FnbSubCategoriesInitiated ->
                      firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
                          this.putString(FirebaseAnalytics.Param.SCREEN_NAME, FNB_SUBCATEGORIES_INITIATED)
                      })

                  is FnbEvent.FnbSubCategoriesError ->
                      firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
                          this.putString(FirebaseAnalytics.Param.SCREEN_NAME, FNB_SUBCATEGORIES_ERROR)
                          this.putString(FirebaseAnalytics.Param.VALUE, event.error)
                      })

                  is FnbEvent.FnbSubCategoriesSuccess ->
                      firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
                          this.putString(FirebaseAnalytics.Param.SCREEN_NAME, FNB_SUBCATEGORIES_SUCCESS)
                      })

                  is FnbEvent.FnbSubCategoriesPresented ->
                      firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
                          this.putString(FirebaseAnalytics.Param.SCREEN_NAME, FNB_SUBCATEGORIES_PRESENTED)
                      })

                  is FnbEvent.FnbSubCategoriesSelected ->
                      firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
                          this.putString(FirebaseAnalytics.Param.SCREEN_NAME, FNB_SUBCATEGORIES_SELECTED)
                      })
              }
          }
      }*/

    fun trackFnb(event: FnbEvent)
    {
        if(isGmsSupported)
        {
            when (event)
            {
                is FnbEvent.FnbCategoriesInitiated ->
                    firebaseAnalytics?.logEvent(FNB_CATEGORIES_INITIATED, null)

                is FnbEvent.FnbCategoriesSuccess ->
                    firebaseAnalytics?.logEvent(FNB_CATEGORIES_SUCCESS, null)

                is FnbEvent.FnbCategoriesError ->
                    firebaseAnalytics?.logEvent(FNB_CATEGORIES_ERROR, Bundle().apply {
                        this.putInt(FirebaseAnalytics.Param.VALUE, event.error)
                    })

                is FnbEvent.FnbCategoriesPresented ->
                    firebaseAnalytics?.logEvent(FNB_CATEGORIES_PRESENTED, null)

                is FnbEvent.FnbCategoriesSelected ->
                    firebaseAnalytics?.logEvent(FNB_CATEGORIES_SELECTED, event.bundle)

                is FnbEvent.FnbSubCategoriesInitiated ->
                    firebaseAnalytics?.logEvent(FNB_SUBCATEGORIES_INITIATED, null)

                is FnbEvent.FnbSubCategoriesError ->
                    firebaseAnalytics?.logEvent(FNB_SUBCATEGORIES_ERROR, Bundle().apply {
                        this.putString(FirebaseAnalytics.Param.ITEM_ID, event.aCategorieId)
                        this.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, event.aCategorieId)
                    })

                is FnbEvent.FnbSubCategoriesSuccess ->
                    firebaseAnalytics?.logEvent(FNB_SUBCATEGORIES_SUCCESS, null)

                is FnbEvent.FnbSubCategoriesPresented ->
                    firebaseAnalytics?.logEvent(FNB_SUBCATEGORIES_PRESENTED, null)

                is FnbEvent.FnbSubCategoriesSelected ->
                    firebaseAnalytics?.logEvent(FNB_SUBCATEGORIES_SELECTED, null)

                is FnbEvent.FnbProductsInitiated ->
                    firebaseAnalytics?.logEvent(FNB_PRODUCTS_INITIATED, null)

                is FnbEvent.FnbProductsError ->
                    firebaseAnalytics?.logEvent(FNB_PRODUCTS_ERROR, Bundle().apply {
                        this.putInt(FirebaseAnalytics.Param.VALUE, event.aError)
                        this.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, event.aCategorieId)
                    })

                is FnbEvent.FnbProductsSuccess ->
                    firebaseAnalytics?.logEvent(FNB_PRODUCTS_SUCCESS, null)

                is FnbEvent.FnbProductsPresented ->
                    firebaseAnalytics?.logEvent(FNB_PRODUCTS_PRESENTED, null)

                is FnbEvent.FnbProductsAdded ->
                    firebaseAnalytics?.logEvent(FNB_PRODUCTS_ADDED, null)

                is FnbEvent.FnbProductQuantityChange ->
                    firebaseAnalytics?.logEvent(FNB_PRODUCTS_QUANTITY_CHANGE, Bundle().apply {
                        this.putInt(FirebaseAnalytics.Param.VALUE, event.aItemCount)
                    })

                is FnbEvent.FnbCartPresented ->
                    firebaseAnalytics?.logEvent(FNB_CART_PRESENTED, null)

                is FnbEvent.FnbCartItemChanged ->
                    firebaseAnalytics?.logEvent(FNB_CART_ITEM_CHANGED, Bundle().apply {
                        this.putString(FirebaseAnalytics.Param.CURRENCY, event.aProduct.currency)
                        this.putString(FirebaseAnalytics.Param.VALUE, event.aProduct.amount)
                        this.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, ""+event.aProduct.categoryId)
                        this.putString(FirebaseAnalytics.Param.ITEM_CATEGORY2, ""+event.aProduct.subCategoryId)
                    })

                is FnbEvent.FnbCartPlacedSuccess ->
                    firebaseAnalytics?.logEvent(FNB_CART_PLACED_SUCCESS, null)

                is FnbEvent.FnbCartPlacedError ->
                    firebaseAnalytics?.logEvent(FNB_CART_PLACED_ERROR, Bundle().apply {
                        this.putString(FirebaseAnalytics.Param.CURRENCY, event.aCurrency)
                        this.putDouble(FirebaseAnalytics.Param.VALUE, event.aTotal)
                        this.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, event.aId)
                        this.putString(FirebaseAnalytics.Param.ITEM_CATEGORY2, event.aSubId)
                    })

                is FnbEvent.FnbBackPressed ->
                    firebaseAnalytics?.logEvent(FNB_CART_BACKPRESS, Bundle().apply {
                        this.putBoolean(FirebaseAnalytics.Param.VALUE, event.aAdded)
                        this.putString(FirebaseAnalytics.Param.VALUE, event.aScreen)
                    })

                // Handle the missing FnbClicked case if it exists
                is FnbEvent.FnbClicked ->
                    firebaseAnalytics?.logEvent(FNB_CLICKED, null)

                // Optional: Handle unknown event types
                else ->
                    firebaseAnalytics?.logEvent("UNKNOWN_EVENT", null)
            }
        }
    }
}

sealed class SipStatusConstant {
    companion object {
        const val SIP_STATUS = "sip_status"
        const val DEVICE_ID = "deviceId"
        const val USER_ID = "userId"
        const val DEVICE_NAME = "deviceName"
        const val CONNECTED = "connected"
    }
}
sealed class  Screen(val name: String) {
    class Login: Screen("Login")
    class FnBCategories: Screen("FnBCategories")
    class Home: Screen("Home")
    class Enrollment: Screen("Enrollment")
    class AddAlarm: Screen("AddAlarm")
    class Alarm: Screen("Alarm")
    class AlarmAlert: Screen("AlarmAlert")
    class SnoozeAlert: Screen("SnoozeAlarm")
    class AmadeusServices: Screen("AmadeusServices")
    class Bluetooth: Screen("Bluetooth")
    class Contacts: Screen("Contacts")
    class ContactsProfile: Screen("ContactsProfile")
    class Covid: Screen("Covid")
    class DarkMode: Screen("DarkMode")
    class DeviceDiscovery: Screen("DeviceDiscovery")
    class PhilipsHue: Screen("PhilipsHue")
    class DeviceLauncher: Screen("DeviceLauncher")
    class Dialer: Screen("Dialer")
    class Feedback: Screen("Feedback")
    class FnBOrder: Screen("FnBOrder")
    class FnBProducts: Screen("FnBProducts")
    class FnBSubCategories: Screen("FnBSubCategories")
    class Interel: Screen("Interel")
    class LiveFeed: Screen("LiveFeed")
    class LiveFeedAudio: Screen("LiveFeedAudio")
    class LiveFeedToHub: Screen("LiveFeedToHub")
    class LiveFeedToHubAudio: Screen("LiveFeedToHubAudio")
    class LiveFeedOwnDevice: Screen("LiveFeedOwnDevice")
    class Music: Screen("Music")
    class NewsAudio: Screen("NewsAudio")
    class NewsDetail: Screen("NewsDetail")
    class NewsHeadline: Screen("NewsHeadline")
    class Notification: Screen("Notification") // TODO
    class Qna: Screen("Qna")
    class Reminder: Screen("Reminder")
    class ReminderAlert: Screen("ReminderAlert")
    class ScreenSaver: Screen("ScreenSaver")

    // Settings
    class Settings: Screen("Settings")
    class CameraControl: Screen("CameraControl") // TODO
    class DeviceSettings: Screen("DeviceSettings")
    class EngineSwitch: Screen("EngineSwitch") // TODO
    class LanguageChange: Screen("LanguageChange") //TODO
    class SimSettings: Screen("SimSettings")
    class ThingsToSay: Screen("ThingsToSay")
    class Update: Screen("Update")
    class WakeWordSwitch: Screen("WakeWordSwitch") // TODO
    class Wifi: Screen("Wifi")

    // Surveillance Mode
    class PinView: Screen("PinView")
    class SurveillanceInit: Screen("SurveillanceInit")
    class SurveillanceOn: Screen("SurveillanceOn")
    class SurveillanceTrigger: Screen("SurveillanceTrigger")

    class Timer: Screen("Timer")
    class ToDo: Screen("ToDo")
    class Translation: Screen("Translation")
    class Transparent: Screen("Transparent") // TODO
    class Video: Screen("Video") // TODO
    class VideoList: Screen("VideoList") // TODO
    class AudioCall: Screen("AudioCall")
    class Weather: Screen("Weather")
    class WebView: Screen("WebView")

    class FaqCategories: Screen("FaqCategories") // TODO
    class FaqQnA: Screen("FaqQnA") // TODO
    class OverviewCategories: Screen("OverviewCategories") // TODO
    class OverviewSubCategories: Screen("OverviewSubCategories") // TODO
    class EventsPackageFragment: Screen("EventsPackageDetails") // TODO
    class EventsExperienceFragment: Screen("EventsExperienceDetails") // TODO
    class EventDetailFragment: Screen("EventDetails") // TODO
    class DestinationDetailFragment: Screen("DestinationDetails") // TODO

}

sealed class EcommerceEvent(val name: String) {
    val CURRENCY = "USD"
    //TODO - Add more ecommerce events
    class ViewItemList(val items: List<String>, val itemListName: String): EcommerceEvent("view_item_list")
    class SelectItem(val itemName: String, val itemPrice: Double, val itemId: String, val category: String = "", val subCategory: String = "") : EcommerceEvent("select_item")
    class AddToCart(val itemName: String, val itemPrice: Double, val itemId: String) : EcommerceEvent("add_to_cart")
    class BeginCheckout(val items: List<String>, val price: Double): EcommerceEvent("begin_checkout")
    class Purchase(val items: List<String>, val transactionId: String? = "", val price: Double): EcommerceEvent("purchase")
    class RemoveFromCart(val itemName: String, val itemPrice: Double, val itemId: String): EcommerceEvent("remove_from_cart")
    class SelectPromotion(val items: List<String>, val promotionId: String? = "", val promotionName: String? = "", val creativeName: String? = "", val creativeSlot: String? = "", val locationId: String? = ""): EcommerceEvent("select_promotion")
    class ViewPromotion(val promotionId: String? = "", val promotionName: String? = "", val creativeName: String? = "", val creativeSlot: String? = "", val locationId: String? = ""): EcommerceEvent("view_promotion")
}


sealed class TourismEvent (val name: String) {
    class SelectItem(val itemName: String?, val itemId: String?)
        : TourismEvent("book_package")
}



val  FNB_CLICKED = "home_fnb_selected"
val  FNB_CATEGORIES_INITIATED = "fnb_categories_initiated"
val  FNB_CATEGORIES_ERROR = "fnb_categories_error"
val  FNB_CATEGORIES_SUCCESS = "fnb_categories_success"
val  FNB_CATEGORIES_PRESENTED = "fnb_categories_presented"
val  FNB_CATEGORIES_SELECTED = "fnb_categories_selected"
val  FNB_SUBCATEGORIES_INITIATED = "fnb_subcategories_initiated"
val  FNB_SUBCATEGORIES_ERROR = "fnb_subcategories_error"
val  FNB_SUBCATEGORIES_SUCCESS = "fnb_subcategories_success"
val  FNB_SUBCATEGORIES_PRESENTED = "fnb_subcategories_presented"
val  FNB_SUBCATEGORIES_SELECTED = "fnb_subcategories_selected"
val  FNB_PRODUCTS_INITIATED = "fnb_products_initiated"
val  FNB_PRODUCTS_ERROR = "fnb_products_error"
val  FNB_PRODUCTS_SUCCESS = "fnb_products_success"
val  FNB_PRODUCTS_PRESENTED = "fnb_products_presented"
val  FNB_PRODUCTS_ADDED = "fnb_product_added"
val  FNB_PRODUCTS_QUANTITY_CHANGE = "fnb_products_quantity_changed"
val  FNB_CART_PRESENTED = "fnb_cart_presented"
val  FNB_CART_ITEM_CHANGED = "fnb_cart_item_changed"
val  FNB_CART_PLACED_SUCCESS = "fnb_cart_placed_success"
val  FNB_CART_PLACED_ERROR = "fnb_cart_placed_error"
val  FNB_CART_BACKPRESS = "fnb_cart_back_press"

val  VALUE_NOT_FOUND = "value_not_found"



sealed class FnbEvent(val name: String) {
    val CURRENCY = "USD"

    class FnbClicked: FnbEvent(FNB_CLICKED)
    class FnbCategoriesInitiated(): FnbEvent(FNB_CATEGORIES_INITIATED)
    class FnbCategoriesError(val error: Int) : FnbEvent(FNB_CATEGORIES_ERROR)
    class FnbCategoriesSuccess(): FnbEvent(FNB_CATEGORIES_SUCCESS)
    class FnbCategoriesPresented(): FnbEvent(FNB_CATEGORIES_PRESENTED)
    class FnbCategoriesSelected(val bundle: Bundle): FnbEvent(FNB_CATEGORIES_SELECTED)
    class FnbSubCategoriesInitiated(): FnbEvent(FNB_SUBCATEGORIES_INITIATED)
    class FnbSubCategoriesError(val error: Int, val aCategorieId: String): FnbEvent(FNB_SUBCATEGORIES_ERROR)
    class FnbSubCategoriesSuccess(): FnbEvent(FNB_SUBCATEGORIES_SUCCESS)
    class FnbSubCategoriesPresented(): FnbEvent(FNB_SUBCATEGORIES_PRESENTED)
    class FnbSubCategoriesSelected(val bundle: Bundle): FnbEvent(FNB_SUBCATEGORIES_SELECTED)
    class FnbProductsInitiated(val bundle: Bundle): FnbEvent(FNB_PRODUCTS_INITIATED)
    class FnbProductsError(val aError: Int, val aCategorieId: String): FnbEvent(FNB_PRODUCTS_ERROR)
    class FnbProductsAdded(val bundle: Product): FnbEvent(FNB_PRODUCTS_ADDED)
    class FnbProductsSuccess(): FnbEvent(FNB_PRODUCTS_SUCCESS)
    class FnbProductsPresented(): FnbEvent(FNB_PRODUCTS_PRESENTED)
    class FnbProductQuantityChange(val aItemCount: Int): FnbEvent(FNB_PRODUCTS_QUANTITY_CHANGE)
    class FnbCartItemChanged(val aProduct: Product) : FnbEvent(FNB_CART_ITEM_CHANGED)
    class FnbCartPresented(): FnbEvent(FNB_CART_PRESENTED)
    class FnbCartPlacedSuccess(val aSuccessMsg: String): FnbEvent(FNB_CART_PLACED_SUCCESS)
    class FnbBackPressed(val aAdded : Boolean, val aScreen:String): FnbEvent(FNB_CART_BACKPRESS)

    class FnbCartPlacedError(val aError:Int, val aId:String?= VALUE_NOT_FOUND, val aSubId:String?= VALUE_NOT_FOUND, val aCurrency:String, val aTotal:Double) : FnbEvent(FNB_CART_PLACED_ERROR)
}