package ru.vanisus;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;



import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class App{
 	public static void main(String[] args) {
		String TOKEN = "5738277062:AAG4D7DN0YLtSsBlfN7jJKN4xiycjaqZsGU";
		Map<Long, User> users = new HashMap<>();
		TelegramBot bot = new TelegramBot(TOKEN);
		Clock clock = Clock.systemDefaultZone();
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		String date = dateFormat.format(new Date());
		bot.setUpdatesListener(updates -> {
			updates.forEach(System.out::println);
				updates.forEach(update -> {
				Long userId = update.message().from().id();
				Document doc = null;
				Element day;
				Element week;
				String div;
				if (!users.containsKey(userId)) {
					bot.execute(new SendMessage(update.message().chat().id(), "Вам необходимо прислать ссылку на расписание вашей группы"));
					users.put(userId, null);
				}
				else if (users.get(userId) == null) {
					String rightUrl = "https://rasp.sstu.ru/rasp/group/";
					String url = update.message().text();
					String testUrl = url.replaceAll("[0-9]", "");
					if (!(testUrl.equals(rightUrl) && urlValidator(url)))
						bot.execute(new SendMessage(update.message().chat().id(), "Вы прислали мне какую-то непонятную ссылку, пришлите ссылку формата \"https://rasp.sstu.ru/rasp/group/\""));
					else {
						User user = new User(userId,update.message().from().firstName(), url);
						users.put(userId, user);
						bot.execute(new SendMessage(update.message().chat().id(), "Принял, отлично, теперь вы можете просматривать свое расписание благодаря мне :)"));

					}

					//bot.execute(new SendMessage(update.message().chat().id(), doc.text()));
				}
				if(update.message().text().equals("Расписание на сегодня"))
				{
					try {
						doc = Jsoup.connect(users.get(update.message().from().id()).getUrl()).get();
					} catch (IOException e) {
						bot.execute(new SendMessage(update.message().chat().id(), "У вас не добавлена ссылка!"));
					}
						day = doc.select("div.day").get(getDayNumberNew(LocalDate.now()));
						div = getDayTimetable(day);
						bot.execute(new SendMessage(update.message().chat().id(), div));

					}
				if (update.message().text().equals("Расписание на завтра")) {
					try {
						doc = Jsoup.connect(users.get(update.message().from().id()).getUrl()).get();
					} catch (IOException e) {
						bot.execute(new SendMessage(update.message().chat().id(), "У вас не добавлена ссылка!"));
					}
					day = doc.select("div.day").get(getDayNumberNew(LocalDate.now()) + 1);
					div = getDayTimetable(day);
					bot.execute(new SendMessage(update.message().chat().id(), div));
				}
				if(update.message().text().equals("Расписание на текущую неделю"))
				{
//					bot.execute(new SendMessage(update.message().chat().id(), "ЫЫЫ"));
					try {
						doc = Jsoup.connect(users.get(update.message().from().id()).getUrl()).get();
					} catch (IOException e) {
						bot.execute(new SendMessage(update.message().chat().id(), "У вас не добавлена ссылка!"));
					}
					week = doc.select("div.week").get(0);
					div = getWeekTimetable(week);
					bot.execute(new SendMessage(update.message().chat().id(), div));
				}

				if(update.message().text().equals("Расписание на следующую неделю")) {
					try {
						doc = Jsoup.connect(users.get(update.message().from().id()).getUrl()).get();
					} catch (IOException e) {
						bot.execute(new SendMessage(update.message().chat().id(), "У вас не добавлена ссылка!"));
					}
					week = doc.select("div.week").get(1);
					div = getWeekTimetable(week);
					bot.execute(new SendMessage(update.message().chat().id(), div));
				}
				if(clock.toString().equals("12:38:00"))
				{
					try {
						doc = Jsoup.connect(users.get(update.message().from().id()).getUrl()).get();
					} catch (IOException e) {
						bot.execute(new SendMessage(update.message().chat().id(), "У вас не добавлена ссылка!"));
					}
					day = doc.select("div.day").get(getDayNumberNew(LocalDate.now()) + 1);
					div = getDayTimetable(day);
					bot.execute(new SendMessage(update.message().chat().id(), "Расписание на завтра!"));
					bot.execute(new SendMessage(update.message().chat().id(), div));
				}


			});
			return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}

	public static void dbStatement(String Statement) {
		try{
			Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
			try (Connection conn = getConnection()){
				java.sql.Statement statement = conn.createStatement();
				int rows = statement.executeUpdate(Statement);
				System.out.println("Added: " + rows);
			}
		}
		catch(Exception ex){
			System.out.println("Connection failed...");
			System.out.println(ex);
		}
	}
	public static Connection getConnection() throws IOException, SQLException {

		Properties props = new Properties();
		try(InputStream in = Files.newInputStream(Path.of(("src/main/resources/database.properties")))) {
			props.load(in);
		}
		String url = props.getProperty("url");
		String username = props.getProperty("username");
		String password = props.getProperty("password");

		return DriverManager.getConnection(url, username, password);
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
		for (int count = 0; count < 4; count++) {
			String resultingLesson = day.select("div.day-lesson").get(count).text();
			if(!(resultingLesson.length() < 1)) {
				dayTimetable.append(resultingLesson);
				dayTimetable.append("\n\n");
			}
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
