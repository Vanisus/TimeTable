package ru.vanisus;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;



import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;

import static ru.vanisus.DAO.*;

public class App {
 	public static void main(String[] args) {
		String TOKEN = "6107692340:AAEUQDi7nTDQAnp7kqvVrhY60QWxjPIe15A";
		TelegramBot bot = new TelegramBot(TOKEN);
//		TimeZone tz = TimeZone.getTimeZone("Europe/Samara");
//		Calendar cal = Calendar.getInstance(tz);
//		cal.set(Calendar.HOUR_OF_DAY, 9);
//		cal.set(Calendar.MINUTE, 19);
//		cal.set(Calendar.SECOND, 0);
//		cal.set(Calendar.MILLISECOND, 0);
//		long delay = cal.getTimeInMillis() - System.currentTimeMillis();
		bot.setUpdatesListener(updates -> {
			updates.forEach(System.out::println);
				updates.forEach(update -> {
				Document doc = null;
				Element day;
				Element week;
				String div;
				bot.execute(new SetMyCommands(new BotCommand("today", "Расписание на сегодня"), new BotCommand("tommorow", "Расписание на завтра"), new BotCommand("thisWeek", "Расписание на текущую неделю"),new BotCommand("nextWeek", "Расписание на следующую неделю")));
				if (!findId(update.message().from().id())) {
						if(update.message().text().equalsIgnoreCase("/start"))
							bot.execute(new SendMessage(update.message().chat().id(), "Вам необходимо прислать ссылку на расписание вашей группы"));
						String url = update.message().text();
						System.out.println(url);
						if(!url.equalsIgnoreCase("/start")) {
							if(urlChecker(update, url)) {
								bot.execute(new SendMessage(update.message().chat().id(), "Принял, отлично, теперь вы можете просматривать свое расписание благодаря мне :)"));
								//User user = new User(update.message().from().id(), update.message().from().firstName(), url);
								addRow(update.message().from().id(), url);
							} else
								bot.execute(new SendMessage(update.message().chat().id(), "Вы прислали мне какую-то непонятную ссылку, пришлите ссылку формата \"https://rasp.sstu.ru/rasp/group/\""));
						}
				}
				if(update.message().text().equalsIgnoreCase("расписание на сегодня") || update.message().text().equalsIgnoreCase("/command1"))
				{
					//bot.execute(new SendMessage(update.message().chat().id() ,"ЛАЛАЛА Я СЛОМАЛСЯ"));
					try {
						doc = Jsoup.connect(getURL(update.message().from().id())).get();
					} catch (IOException e) {
						bot.execute(new SendMessage(update.message().chat().id(), "У вас не добавлена ссылка!"));
					}
						day = doc.select("div.day").get(getDayNumberNew(LocalDate.now()));
						div = getDayTimetable(day);
						bot.execute(new SendMessage(update.message().chat().id(), div));

					}
				if (update.message().text().equalsIgnoreCase("расписание на завтра") || update.message().text().equalsIgnoreCase("/command2")) {
					try {
						doc = Jsoup.connect(getURL(update.message().from().id())).get();
					} catch (IOException e) {
						bot.execute(new SendMessage(update.message().chat().id(), "У вас не добавлена ссылка!"));
					}
					day = doc.select("div.day").get(getDayNumberNew(LocalDate.now()) + 1);
					div = getDayTimetable(day);
					bot.execute(new SendMessage(update.message().chat().id(), div));
				}
				if(update.message().text().equalsIgnoreCase("расписание на текущую неделю") || update.message().text().equalsIgnoreCase("/command3"))
				{
//					bot.execute(new SendMessage(update.message().chat().id(), "ЫЫЫ"));
					try {
						doc = Jsoup.connect(getURL(update.message().from().id())).get();
					} catch (IOException e) {
						bot.execute(new SendMessage(update.message().chat().id(), "У вас не добавлена ссылка!"));
					}
					week = doc.select("div.week").get(0);
					div = getWeekTimetable(week);
					bot.execute(new SendMessage(update.message().chat().id(), div));
				}

				if(update.message().text().equalsIgnoreCase("расписание на следующую неделю") || update.message().text().equalsIgnoreCase("/command4")) {
					try {
						doc = Jsoup.connect(getURL(update.message().from().id())).get();
					} catch (IOException e) {
						bot.execute(new SendMessage(update.message().chat().id(), "У вас не добавлена ссылка!"));
					}
					try {
						week = doc.select("div.week").get(1);
						div = getWeekTimetable(week);
						bot.execute(new SendMessage(update.message().chat().id(), div));
					} catch (IndexOutOfBoundsException e) {
						bot.execute(new SendMessage(update.message().chat().id(), "Расписание на следующую неделю пока недоступно :)"));
					}

				}
//				if(update.message().text().equalsIgnoreCase("обновить ссылку")) {
//					bot.execute(new SendMessage(update.message().chat().id(), "Хорошо, пришлите мне новую ссылку"));
//					String url = update.message().text();
//					System.out.println(url);
//					if(!url.equalsIgnoreCase("обновить ссылку"))
//						if(urlChecker(update, url)) {
//							updateURL(update.message().from().id(), url);
//							bot.execute(new SendMessage(update.message().chat().id(), "Принял, отлично, ссылка обновлена :)"));}
//						else
//							bot.execute(new SendMessage(update.message().chat().id(), "Вы прислали мне какую-то непонятную ссылку, пришлите ссылку формата \"https://rasp.sstu.ru/rasp/group/\""));
//					}

//					TimerTask task = new TimerTask() {
//					@Override
//					public void run() {
//						Document doc = null;
//						Element day;
//						String div;
//						try {
//							doc = Jsoup.connect(getURL(update.message().from().id())).get();
//						} catch (IOException e) {
//							bot.execute(new SendMessage(update.message().chat().id(), "У вас не добавлена ссылка!"));
//						}
//						day = doc.select("div.day").get(getDayNumberNew(LocalDate.now()) + 1);
//						div = getDayTimetable(day);
//						bot.execute(new SendMessage(update.message().chat().id(), div));
//				} };
//				Timer timer = new Timer();
//				timer.schedule(task,delay);


			});
			return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}

//	public static ReplyKeyboardMarkup createKeyboard() {
//		KeyboardButton today = new KeyboardButton("расписание на сегодня");
//		KeyboardButton tomorrow = new KeyboardButton("расписание на завтра");
//		KeyboardButton thisWeek = new KeyboardButton("расписание на текущую неделю");
//		KeyboardButton nextWeek = new KeyboardButton("расписание на следующую неделю");
//		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(today, tomorrow, thisWeek, nextWeek);
//		return replyKeyboardMarkup;
//
//
//	 }

	public static Boolean urlChecker(Update update, String url) {
		String rightUrl = "https://rasp.sstu.ru/rasp/group/";
		String testUrl = url.replaceAll("[0-9]", "");
		if (!(testUrl.equals(rightUrl) && urlValidator(url) && url.length() != 0))
			return false;
		else
			return  true;
	}
	public static boolean urlValidator(String url)
	{
		try {
			new URL(url).toURI();
			return true;
		}
		catch (URISyntaxException | MalformedURLException exception) {
			return false;
		}
	}

	 public static int getDayNumberNew(LocalDate date) {
		DayOfWeek day = date.getDayOfWeek();
		return day.getValue();
	}

	public static String getDayOfWeek(LocalDate date)
	{
		DayOfWeek day = date.getDayOfWeek();
		return day.toString();
	}

	public static String getDayTimetable(Element day)
	{
		StringBuilder dayTimetable = new StringBuilder();
		try {
			for (int count = 0; count < day.select("div.day-lesson").size(); count++) {
				String resultingLesson = day.select("div.day-lesson").get(count).text();
				if(!(resultingLesson.length() < 1)) {
					dayTimetable.append(resultingLesson);
					dayTimetable.append("\n\n");
				}
			}
		} catch (NullPointerException e)
		{
			System.out.println("В этом дне только 4 пары");
		}
		//String resultDay = new String(dayTimetable);
		return dayTimetable.toString();
	}

	public static String getWeekTimetable(Element week) {
		 StringBuilder weekTimetable = new StringBuilder();
		 for(int count = 1; count < 7; count++) {
			 StringBuilder resultingDay = new StringBuilder();
			 Element dayE = week.select("div.day").get(count);
			 String dateOfTheDay = dayE.select("div.day > div.day-header").get(0).text();
			 if (dateOfTheDay.length() != 0) {
				 weekTimetable.append("// " + dateOfTheDay + "// ");
				 weekTimetable.append("\n\n");
				 resultingDay.append(getDayTimetable(dayE));
				 weekTimetable.append(resultingDay);
				 weekTimetable.append("\n\n");
			 }
		 }
		 return weekTimetable.toString();
	}
}
