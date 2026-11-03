package com.sb.woohoo.ui.main.fragments.reminders;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.LoggerWrapper;
import com.sb.woohoo.ui.base.BasePresenter;
import com.sb.woohoo.ui.main.Categories;
import com.sb.woohoo.ui.main.fragments.reminders.broadcastreceiver.ReminderReceiver;
import com.sb.woohoo.ui.main.fragments.reminders.broadcastreceiver.SetReminder;
import com.sb.woohoo.ui.main.fragments.reminders.pojo.Reminder;
import com.sb.woohoo.ui.main.pojo.IntentInfo;

import org.apache.commons.collections4.map.ListOrderedMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import javax.inject.Inject;

import static android.content.Context.ALARM_SERVICE;
import static com.sb.woohoo.utils.others.MethodUtils.flagForPendingIntent;

public class ReminderPresenter extends BasePresenter implements ReminderContract.Action {

    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm";
    private static int TYPE_HEADER = 0;
    private static int TYPE_SINGLE = 1;
    private static int TYPE_MIDDLE = 2;
    private static int TYPE_TOP = 3;
    private static int TYPE_BOTTOM = 4;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
    private ReminderFragment reminderView;
    private List<Reminder> reminderUIList; // UI List, This list is passed to the recycler adapter
    private ListOrderedMap<String, Integer> hashMap; // Contains the index to each headers in the UI LIST
    private ReminderRepository reminderRepository;

    @Inject
    public ReminderPresenter(ReminderFragment reminderFragment,
                             ReminderRepository reminderRepository,
                             List<Reminder> reminderUIList) {
        this.reminderView = reminderFragment;
        this.reminderRepository = reminderRepository;
        reminderRepository.onAttach(this);
        hashMap = new ListOrderedMap<>();
        this.reminderUIList = reminderUIList;
    }

    /**
     * Accepts date in format yyyy-MM-dd'T'HH:mm
     * Returns time in format HH:mm AM/PM
     */
    @SuppressLint("DefaultLocale")
    public static String getTime(String dateTime) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(dateFormatter.parse(dateTime));

           String hour = (c.get(Calendar.HOUR) == 0) ?"12":c.get(Calendar.HOUR)+"";
            return hour + ":" + String.format("%02d", c.get(Calendar.MINUTE)) + " " + (c.get(Calendar.AM_PM) == 0 ? "AM" : "PM");


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addReminder(Reminder newReminder, int actionReminder) {
        // Capitalize name of reminder.
        String title = newReminder.getReminder();
        title = title.substring(0, 1).toUpperCase() + title.substring(1);
        newReminder.setReminder(title);

        if (reminderUIList != null && reminderUIList.size() == 0) {
            reminderRepository.getRemindersList(actionReminder, null);
        }

        if (validateReminder(newReminder)) {
            //Add the new reminder into the server
            //Convert the time TO UTC before sending the data to the server
            newReminder.setUTCTime(convertLocalToUTCDate(newReminder.getDateTime()));
            reminderRepository.addReminder(newReminder);
            reminderView.respondVoice(String.format(reminderView.getString(R.string.you_remind_to), newReminder.getReminder(),newReminder.getDate(),getTime(newReminder.getDateTime())));
        } else {
            reminderView.respondVoice(reminderView.getString(R.string.reminder_date_already_paused));
        }
    }

    private String convertLocalToUTCDate(String date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormatter.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date time = calendar.getTime();
        return dateFormatter.format(time);
    }

    private Date convertUTCToLocalDate(String date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
        Date inputDate = new Date();
        if (date != null && !date.isEmpty()) {
            try {
                inputDate = dateFormatter.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return inputDate;
    }

    private boolean validateReminder(Reminder newReminder) {

        Calendar c = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();

        try {
            c.setTime(dateFormatter.parse(newReminder.getDateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c.after(currentDate);
    }

    /**
     * Method to display all the reminders from the api
     *
     * @param reminderAction Intent action
     */
    @Override
    public void showReminders(int reminderAction) {
        if (reminderUIList != null && reminderUIList.size() == 0) {
            reminderRepository.getRemindersList(reminderAction, null);
        }
    }

    /**
     * Method to set the ALARM
     *
     * @param newReminder Object of the new reminder
     */
    @Override
    public void setAlarm(Reminder newReminder) {
        int position = addNewReminder(newReminder);
        int reminderID = Integer.parseInt(newReminder.getReminderId());
        reminderView.scrollList(position);
        SetReminder.setReminder(reminderView.getContext(), ReminderReceiver.class, newReminder, reminderID);
    }

    /**
     * Method to delete the reminder from the API
     */
    @Override
    public void deleteReminders() {
        reminderRepository.deleteReminders();
    }

    @Override
    public void cancelAlarm(int reminderID) {
        Intent intent = new Intent(reminderView.getContext(), ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(reminderView.getContext(), reminderID, intent, flagForPendingIntent());
        AlarmManager alarmManager = (AlarmManager) Objects
                .requireNonNull(reminderView.getActivity())
                .getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    public void deleteReminder(Reminder reminder) {

        boolean found = false;
        for (int i = 0; i < reminderUIList.size(); i++) {
            if (reminderUIList.get(i).getReminder() != null) {
                if (Objects.equals(reminderUIList.get(i).getReminder().toLowerCase(), reminder.getReminder().toLowerCase())) {
                    deleteReminderFromUI(i);
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            reminderView.respondVoice(String.format(reminderView.getString(R.string.you_dont_have_as_your_reminder),reminder.getReminder()));
        } else {
            reminderView.respondVoice(reminderView.getString(R.string.reminder_deleted));
        }
    }

    @Override
    public void editReminder(Reminder reminder, HashMap<String, String> parameters) {
        String oldTitle = parameters.get("old_title");
        String newTitle = parameters.get("new_title");

        // Capitalize new title of Reminder
        if (newTitle != null) {
            newTitle = newTitle.substring(0, 1).toUpperCase() + newTitle.substring(1);
        }

        if (reminderUIList != null && reminderUIList.size() != 0) {
            boolean found = false;
            for (int i = 0; i < reminderUIList.size(); i++) {
                if (reminderUIList.get(i).getReminder() != null) {
                    if (reminderUIList.get(i).getReminder().equalsIgnoreCase(oldTitle)) {
                        reminderUIList.get(i).setReminder(newTitle);

                        reminderRepository.editReminder(reminderUIList.get(i));

                        reminderView.scrollList(i);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                reminderView.respondVoice(String.format(reminderView.getString(R.string.sorry_you_dont_have_reminder_set),oldTitle));
            } else {
                reminderView.respondVoice(String.format(reminderView.getString(R.string.is_changed_to),oldTitle,newTitle));
            }
        } else {
            reminderRepository.getRemindersList(Categories.Skills.Reminder.REMINDER_EDIT, null);
        }
    }

    /**
     * Method to populate the date in the list of reminders
     */
    private void populateDate(Reminder reminder) {
        if (reminder != null) {
            reminder.setReminderDateTime(convertUTCToLocalDate(reminder.getDateTime()));
            reminder.setDateTime(dateFormatter.format(reminder.getReminderDateTime()));
        }
    }

    /**
     * Accepts date in format yyyy-MM-dd'T'HH:mm
     * Returns date in format dd MMM yyyy
     */
    private String getDate(String dateTime) {

        Calendar c = Calendar.getInstance();

        try {
            c.setTime(dateFormatter.parse(dateTime));
            Formatter f = new Formatter(Locale.getDefault());
            String monthName = f.format("%tb", c).toString();
            int day = c.get(Calendar.DATE);
            int year = c.get(Calendar.YEAR);

            return day + " " + monthName + " " + year;


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method to check DATE if @sDate1 is before @sDate, returns true/false
     */
    private boolean checkBeforeDate(String sDate1, String sDate2) {
        try {
            Date date1 = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(sDate1);
            Date date2 = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(sDate2);
            return date1.before(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Method to check DATE if @sDate1 is after @sDate, returns true/false
     */
    private boolean checkAfterDate(String sDate1, String sDate2) {
        try {
            Date date1 = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(sDate1);
            Date date2 = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(sDate2);
            return date1.after(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Method to check TIME if @sDate1 is before @sDate2, returns true/false
     */
    private boolean compareBeforeTime(String sDate1, String sDate2) {
        try {
            Date date1 = dateFormatter.parse(sDate1);
            Date date2 = dateFormatter.parse(sDate2);
            return date1.before(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
    Adds a new reminder in the UI List
     */
    private int addNewReminder(Reminder reminder) {
        int position;
        String reminderDateTime = reminder.getDateTime();

        //Check if the @reminder is from the new day, if true add a new view into the UI List
        if (!hashMap.containsKey(getDate(reminderDateTime))) {
            for (int i = 0; i < hashMap.size(); i++) {  //The new reminder should be added into the sorted list
                if (checkBeforeDate(getDate(reminderDateTime), hashMap.get(i))) {  // Compares the new reminder with the reminders in the hashmap list so it can fit into proper position. This compares if the position is before the reminder in the list

                    position = hashMap.getValue(i);
                    hashMap.put(i, getDate(reminderDateTime), hashMap.getValue(i));
                    addNewReminderAtIndex(reminder, position, TYPE_HEADER);    // Since the reminder is a new view in the list it should have a header

                    addNewReminderAtIndex(reminder, position + 1, TYPE_SINGLE); // Since the reminder is the only view for that header so TYPE = TYPE_SINGLE

                    //Hashmap should be updated since the UI list has added two new items i.e. HEADER and ITEM
                    for (int k = i + 1; k < hashMap.size(); k++)
                        hashMap.setValue(k, hashMap.getValue(k) + 2);

                    return position + 1; // return the position added.
                }
            }

            //If the hash map has no values i.e. UI List is empty in the initial stage, add the new reminder i.e. Header and RemoteAlertModel into the first position
            if (hashMap.size() == 0) {
                hashMap.put(getDate(reminderDateTime), 0);

                addNewReminderAtIndex(reminder, 0, TYPE_HEADER);
                addNewReminderAtIndex(reminder, 1, TYPE_SINGLE);

                return 1;
            } else { // Insert the new reminder into the last position of the UI List
                if (checkAfterDate(getDate(reminderDateTime), hashMap.get(hashMap.size() - 1))) {
                    position = reminderUIList.size();

                    addNewReminderAtIndex(reminder, position, TYPE_HEADER);
                    addNewReminderAtIndex(reminder, position + 1, TYPE_SINGLE);

                    hashMap.put(getDate(reminderDateTime), position);

                    return position + 1;
                }
            }

        } else { // Else if the reminder's date is already present in the UI List

            int headerPosition = hashMap.get(getDate(reminderDateTime));
            int itemPosition = headerPosition + 1;
            int i;

            //Check if the list has single reminder for the particular day
            if (reminderUIList.size() <= 0) {
                addNewReminderAtIndex(reminder, 0, TYPE_SINGLE);
                return 0;
            }
            if (itemPosition < reminderUIList.size()) {
                if (reminderUIList.get(itemPosition).getType() == TYPE_SINGLE) {
                    //The new reminder can either be Top or Bottom
                    //Comparing the new reminder with the time with present reminder
                    if (compareBeforeTime(reminderDateTime, reminderUIList.get(itemPosition).getDateTime())) {
                        changedReminderType(itemPosition, TYPE_BOTTOM);
                        addNewReminderAtIndex(reminder, itemPosition, TYPE_TOP);
                        updateHeaderTable(reminderDateTime);
                        return itemPosition;
                    } else { // Inserting in the last position of the list
                        changedReminderType(itemPosition, TYPE_TOP);
                        addNewReminderAtIndex(reminder, itemPosition + 1, TYPE_BOTTOM);
                        updateHeaderTable(reminderDateTime);
                        return itemPosition + 1;
                    }
                } else { // if there are multiple entries for that particular day
                    // the new reminder should be sorted with the time and the loop will iterate until it finds a header
                    for (i = itemPosition; TYPE_HEADER != reminderUIList.get(i).getType(); i++) {
                        //comparing the time of the new reminder with the present reminder
                        if (compareBeforeTime(reminderDateTime, reminderUIList.get(i).getDateTime())) {
                            if (reminderUIList.get(i).getType() == TYPE_TOP) {
                                changedReminderType(i, TYPE_MIDDLE);
                                addNewReminderAtIndex(reminder, i, TYPE_TOP);
                            } else {
                                addNewReminderAtIndex(reminder, i, TYPE_MIDDLE);
                            }
                            updateHeaderTable(reminderDateTime);
                            return i;
                        }

                        //Checking if the next i value has reached the UI List size.
                        if ((i + 1) == reminderUIList.size()) {
                            i++;
                            break;
                        }
                    }
                    //if i reaches the size of UI List then insert the new reminder to end of the list
                    changedReminderType(i - 1, TYPE_MIDDLE);
                    addNewReminderAtIndex(reminder, i, TYPE_BOTTOM);

                    updateHeaderTable(reminderDateTime);
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * This method is used to changed the type of the previous reminder in the UI List
     *
     * @param position : Which position to be altered
     * @param type     : TYPE_TOP, TYPE_MIDDLE, TYPE_BOTTOM
     */
    private void changedReminderType(int position, int type) {

        reminderUIList.get(position).setType(type);

    }

    /**
     * Method is used to add a new reminder into the UI List into the specific sorted position
     *
     * @param reminder : DeviceData Source
     * @param position : At which index position to add the data
     * @param type     : TYPE_HEADER, TYPE_SINGLE, TYPE_TOP, TYPE_MIDDLE, TYPE_BOTTOM
     */
    private void addNewReminderAtIndex(Reminder reminder, int position, int type) {
        if (type == TYPE_HEADER) {
            Reminder reminderObject = new Reminder();
            reminderObject.setReminderDate(getDate(reminder.getDateTime()));
            reminderObject.setType(type);
            reminderObject.setDateTime(reminder.getDateTime());
            reminderObject.setReminderId(reminder.getReminderId());
            reminderUIList.add(position, reminderObject);
        } else {
            Reminder reminderObject = new Reminder();
            reminderObject.setDateTime(reminder.getDateTime());
            reminderObject.setReminderTime(getTime(reminder.getDateTime()));
            reminderObject.setType(type);
            reminderObject.setReminderId(reminder.getReminderId());
            reminderObject.setReminderDate(getDate(reminder.getDateTime()));
            reminderObject.setReminder(reminder.getReminder());
            reminderUIList.add(position, reminderObject);
        }
    }

    /**
     * Method to update the hash table that contains the index of the UI List of each header
     *
     * @param reminder : Reminder DeviceData to update the next header fields
     */
    private void updateHeaderTable(String reminder) {
        String nextHeader = hashMap.nextKey(getDate(reminder));
        if (nextHeader != null) {
            int index = hashMap.indexOf(nextHeader);
            for (int k = index; k < hashMap.size(); k++)
                hashMap.setValue(k, hashMap.getValue(k) + 1);
        }
    }

    /**
     * Method to create the UI LIST
     */
    void sortReminders(List<Reminder> reminderList, int reminderAction, IntentInfo intentInfo) {
        if (reminderAction == Categories.Skills.Reminder.REMINDER_SHOW) {
            if (reminderList.size() != 0) {
                if (reminderList.size() == 1) {
                    reminderView.respondVoice(reminderView.getString(R.string.one_reminder_set));
                } else {
                    reminderView.respondVoice(String.format(reminderView.getString(R.string.you_have_reminder_set),reminderList.size()));
                }
            } else {
                reminderView.respondVoice(reminderView.getString(R.string.no_reminders_set));
            }
        }
        // Populating date objects
        for (Reminder currentReminder : reminderList) {
            populateDate(currentReminder);
        }

        // Sorting with date objects
        Collections.sort(reminderList);

        //hashmap where date is the key and UI Index is the value
        int j;
        for (int i = 0; i < reminderList.size(); i = j) {
            String date = getDate(reminderList.get(i).getDateTime());
            hashMap.put(date, i);

            hashMap.setValue(hashMap.indexOf(date), hashMap.indexOf(date) + hashMap.getValue(hashMap.indexOf(date)));
            for (j = i + 1; j < reminderList.size(); j++)
                if (!Objects.equals(date, getDate(reminderList.get(j).getDateTime()))) {
                    break;
                }
        }

        //hash map to handle the data list
        ListOrderedMap<String, Integer> listOrderedMap = new ListOrderedMap<>();
        int k;
        for (int i = 0; i < reminderList.size(); i = k) {
            String date = getDate(reminderList.get(i).getDateTime());
            listOrderedMap.put(date, i);
            for (k = i + 1; k < reminderList.size(); k++)
                if (!Objects.equals(date, getDate(reminderList.get(k).getDateTime()))) {
                    break;
                }
        }

        int value = 0;
        int count = 0;
        int index = -1;

        //Add the reminders into the UI List
        for (int i = 0; i < reminderList.size(); i++) {
            if (listOrderedMap.get(getDate(reminderList.get(i).getDateTime())) == i) { // if the i matches the value from the hash map then type = TYPE_HEADER

                addNewReminderAtIndex(reminderList.get(i), ++index, TYPE_HEADER);
                addNewReminderAtIndex(reminderList.get(i), ++index, TYPE_SINGLE);// the first element after header will be single
                value = listOrderedMap.get(getDate(reminderList.get(i).getDateTime()));// get the current header value
                count = 1;

            } else if (value == listOrderedMap.get(getDate(reminderList.get(i).getDateTime()))) { // if the current header matches with the list then it becomes a item for that header
                if (count == 1) { // count compares if the current item is the second in the list

                    //Change the type of the previous reminder
                    reminderUIList.get(index).setType(TYPE_TOP);

                    addNewReminderAtIndex(reminderList.get(i), ++index, TYPE_BOTTOM); // adds the current item to the bottom of the list
                    count++;

                } else if (count >= 2) { // count compares if the there is at least two items in the list

                    reminderUIList.get(index).setType(TYPE_MIDDLE);

                    addNewReminderAtIndex(reminderList.get(i), ++index, TYPE_BOTTOM); //adds the current item to the bottom of the list
                    count++;
                }
            } else
                LoggerWrapper.i(ReminderPresenter.class.getSimpleName(), reminderList.get(i).getDateTime());
        }

        if (null != intentInfo) {
            Reminder reminder = (Reminder) intentInfo.getIntentPojo();

            if (reminderAction == Categories.Skills.Reminder.REMINDER_DELETE_TITLE_ALTERNATIVE || reminderAction == Categories.Skills.Reminder.REMINDER_DELETE_TITLE) {
                if (reminderList.size() != 0) {
                    deleteReminder(reminder);
                } else {
                    reminderView.respondVoice(reminderView.getString(R.string.no_reminders_set));
                }

            } else if (reminderAction == Categories.Skills.Reminder.REMINDER_EDIT_ALTERNATIVE || reminderAction == Categories.Skills.Reminder.REMINDER_EDIT) {

                if (reminderList.size() != 0) {
                    editReminder(reminder, intentInfo.getParameters());
                } else {
                    reminderView.respondVoice(reminderView.getString(R.string.no_reminders_set));
                }
            }
        }
        reminderView.allReminders(reminderUIList); // update the adapter

    }

    /**
     * Delete the reminder from the UI and the server
     */

    void deleteReminderFromUI(int position) {

        //Deleting the reminder from api
        reminderRepository.deleteReminder(reminderUIList.get(position).getReminderId(), 0);

        String header = reminderUIList.get(position).getReminderDate();
        int headerPosition = hashMap.getValue(hashMap.indexOf(header));
        String nextHeader = hashMap.nextKey(header);
        int nextHeaderPosition = hashMap.indexOf(nextHeader);
        int type = reminderUIList.get(position).getType();
        int previousType = reminderUIList.get(position - 1).getType();
        int nextType = -1;

        if (position + 1 != reminderUIList.size())
            nextType = reminderUIList.get(position + 1).getType();

        if (TYPE_SINGLE == type) {

            //Since there is a single item in particular day, delete the header too
            reminderUIList.remove(headerPosition);
            //Since the item gets shifted up when deleted from the list, delete the header position again
            reminderUIList.remove(headerPosition);

            if (nextHeaderPosition != -1) // checking if there is any other reminders in the hash map
                for (int k = nextHeaderPosition; k < hashMap.size(); k++)
                    hashMap.setValue(k, hashMap.getValue(k) - 2); // subtract two position since header and item gets removed

            hashMap.remove(header); // remove the header from the hasp map since it does not contain any reminders for that day
        } else if (TYPE_BOTTOM == type) { // checking if the reminder is at the bottom
            if (TYPE_TOP == previousType) { // checking if the previous reminder is at top then the item becomes single

                changedReminderType(position - 1, TYPE_SINGLE);
                reminderUIList.remove(position);

                if (nextHeaderPosition != -1)
                    for (int k = nextHeaderPosition; k < hashMap.size(); k++)
                        hashMap.setValue(k, hashMap.getValue(k) - 1);

            } else if (TYPE_MIDDLE == previousType) {

                changedReminderType(position - 1, TYPE_BOTTOM);
                reminderUIList.remove(position);

                if (nextHeaderPosition != -1)
                    for (int k = nextHeaderPosition; k < hashMap.size(); k++)
                        hashMap.setValue(k, hashMap.getValue(k) - 1);

            } else {
                LoggerWrapper.i(ReminderPresenter.class.getSimpleName(), String.valueOf(reminderUIList.get(position).getType()));
            }
        } else if (type == TYPE_TOP) { // checking if the deleted item is at the top then change then next reminder
            if (nextType == TYPE_BOTTOM) {
                changedReminderType(position + 1, TYPE_SINGLE);
                reminderUIList.remove(position);
                if (nextHeaderPosition != -1)
                    for (int k = nextHeaderPosition; k < hashMap.size(); k++)
                        hashMap.setValue(k, hashMap.getValue(k) - 1);
            } else if (nextType == TYPE_MIDDLE) {
                changedReminderType(position + 1, TYPE_TOP);
                reminderUIList.remove(position);
                if (nextHeaderPosition != -1)
                    for (int k = nextHeaderPosition; k < hashMap.size(); k++)
                        hashMap.setValue(k, hashMap.getValue(k) - 1);
            }
        } else {
            reminderUIList.remove(position);
            if (nextHeaderPosition != -1)
                for (int k = nextHeaderPosition; k < hashMap.size(); k++)
                    hashMap.setValue(k, hashMap.getValue(k) - 1);
        }

        reminderView.scrollList(-1);
    }

    void updateReminderAfterDelete() {
        for (int i = 0; i < reminderUIList.size(); i++) {
            if (reminderUIList.get(i).getType() != TYPE_HEADER) {
                Intent intent = new Intent(reminderView.getContext(), ReminderReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(reminderView.getContext(), Integer.parseInt(reminderUIList.get(i).getReminderId()), intent, flagForPendingIntent());
                AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(reminderView.getActivity()).getSystemService(ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.cancel(pendingIntent);
                }
            }
        }

        reminderUIList.clear();
        reminderView.scrollList(-1);
    }

    private void deleteEditReminder(int action, IntentInfo intentInfo, Reminder reminder) {
        if (reminderUIList != null && reminderUIList.size() == 0) {
            reminderRepository.getRemindersList(action, intentInfo);
        } else if (intentInfo.getAction() == Categories.Skills.Reminder.REMINDER_EDIT ||
                intentInfo.getAction() == Categories.Skills.Reminder.REMINDER_EDIT_ALTERNATIVE) {
            editReminder(reminder, intentInfo.getParameters());
        } else if (intentInfo.getAction() == Categories.Skills.Reminder.REMINDER_DELETE_TITLE ||
                intentInfo.getAction() == Categories.Skills.Reminder.REMINDER_DELETE_TITLE_ALTERNATIVE) {
            deleteReminder(reminder);
        }
    }

    @Override
    public void processActions(IntentInfo intentInfo) {
        if(intentInfo==null){
            showReminders(Categories.Skills.Reminder.REMINDER_SHOW);
        }else {
            Reminder reminder = (Reminder) intentInfo.getIntentPojo();
            if (intentInfo.getAction() == Categories.Skills.Reminder.REMINDER_SHOW) {
                showReminders(intentInfo.getAction());
            } else if (intentInfo.getAction() == Categories.Skills.Reminder.REMINDER_ADD) {
                addReminder(reminder, intentInfo.getAction());
            } else if (intentInfo.getAction() == Categories.Skills.Reminder.REMINDER_DELETE_TITLE) {
                deleteEditReminder(intentInfo.getAction(), intentInfo, reminder);
            } else if (intentInfo.getAction() == Categories.Skills.Reminder.REMINDER_DELETE_ALL) {
                deleteReminders();
            } else if (intentInfo.getAction() == Categories.Skills.Reminder.REMINDER_EDIT) {
                deleteEditReminder(intentInfo.getAction(), intentInfo, reminder);
            } else if (intentInfo.getAction() == Categories.Skills.Reminder.REMINDER_DELETE_TITLE_ALTERNATIVE) {
                deleteEditReminder(intentInfo.getAction(), intentInfo, reminder);
            } else if (intentInfo.getAction() == Categories.Skills.Reminder.REMINDER_EDIT_ALTERNATIVE) {
                deleteEditReminder(intentInfo.getAction(), intentInfo, reminder);
            }
        }
    }

    @Override
    public void presenterCleanUps() {
        super.presenterCleanUps();
        reminderRepository.repositoryCleanUps();
    }
}


