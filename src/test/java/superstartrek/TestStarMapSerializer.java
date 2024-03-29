package superstartrek;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import superstartrek.client.activities.Presenter;
import superstartrek.client.activities.View;
import superstartrek.client.activities.pwa.Callback;
import superstartrek.client.control.GameController;
import superstartrek.client.control.ScoreKeeper;
import superstartrek.client.persistence.StarMapSerialiser;
import superstartrek.client.space.Constants;
import superstartrek.client.space.Quadrant;
import superstartrek.client.space.Setting;
import superstartrek.client.space.Setup;
import superstartrek.client.space.Thing;
import superstartrek.client.utils.BrowserAPI;
import superstartrek.client.vessels.Klingon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Random;

public class TestStarMapSerializer extends BaseTest {

	@Test
	public void test() {
		Setup setup = new Setup(application);
		final Random random = new Random(123);
		application.browserAPI = new BrowserAPI() {

			@Override
			public Void storeValueLocally(String key, String value) {
				return null;
			}

			@Override
			public void setCookie(String name, String value) {
			}

			@Override
			public Void reloadApplication() {
				return null;
			}

			@Override
			public Void postHistoryChange(String token, boolean issueEvent) {
				return null;
			}

			@Override
			public Void postHistoryChange(String token) {
				return null;
			}

			@Override
			public int randomInt(int upperBound) {
				return random.nextInt(upperBound);
			}

			@Override
			public double randomDouble() {
				return random.nextDouble();
			}

			@Override
			public boolean hasKeyboard() {
				return false;
			}

			@Override
			public int getWindowWidthPx() {
				return 0;
			}

			@Override
			public int getWindowHeightPx() {
				return 0;
			}

			@Override
			public String getParameter(String param) {
				return null;
			}

			@Override
			public int getMetricWidthInPx() {
				return 0;
			}

			@Override
			public int getMetricHeightInPx() {
				return 0;
			}

			@Override
			public String getLocallyStoredValue(String key) {
				return null;
			}

			@Override
			public String getCookie(String name) {
				return null;
			}

			@Override
			public void deleteValueLocally(String key) {
			}

			@Override
			public Element createElementNs(String nameSpace, String tag) {
				return null;
			}

			@Override
			public Void confirm(String message, Callback<Boolean> answer) {
				return null;
			}

			@SuppressWarnings("rawtypes")
			@Override
			public <P extends Presenter> void addToPage(View<P> view) {
			}

			@Override
			public HandlerRegistration addHistoryHandler(ValueChangeHandler<String> handler) {
				return null;
			}

			@Override
			public Void addGlobalCss(String css) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Void removeGlobalCss(String css) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public HandlerRegistration addWindowResizeHandler(ResizeHandler handler) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Void alert(String message) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getAppBuildNr() {
				// TODO Auto-generated method stub
				return null;
			}

		};
		application.starMap = starMap = setup.createNewMap();
		application.gameController = mock(GameController.class);
		ScoreKeeper sc = mock(ScoreKeeper.class);
		when(application.gameController.getScoreKeeper()).thenReturn(sc);
		when(sc.getScore()).thenReturn(123);
		StarMapSerialiser serialiser = new StarMapSerialiser(application);
		String json = serialiser.serialise(starMap);
		JSONObject obj = new JSONObject(json);
		for (int x = 0; x < Constants.SECTORS_EDGE; x++)
			for (int y = 0; y < Constants.SECTORS_EDGE; y++) {
				Quadrant q = starMap.getQuadrant(x, y);
				JSONObject qJs = obj.getJSONArray("quadrants").getJSONObject(x + y * Constants.SECTORS_EDGE);
				assertEquals(q.x, qJs.getInt("x"));
				assertEquals(q.y, qJs.getInt("y"));
				assertEquals(q.getName(), qJs.getString("name"));
				JSONArray thingsJs = qJs.getJSONArray("things");
				for (int i = 0; i < thingsJs.length(); i++) {
					JSONObject thingJs = thingsJs.getJSONObject(i);
					Thing thing = q.findThingAt(thingJs.getInt("x"), thingJs.getInt("y"));
					assertNotNull(thingJs.toString(), thing);
					assertEquals(thing.getName(), thingJs.getString("name"));
					if (thing instanceof Klingon) {
						Klingon klingon = thing.as();
						Setting disruptor = klingon.getDisruptor();
						JSONObject disruptorJs = thingJs.getJSONObject("disruptor");
						assertEquals(disruptor.getValue(), disruptorJs.getDouble("value"), 0.1);
						assertEquals(klingon.getShipClass().name(), thingJs.get("shipclass").toString());
					}
				}
			}
		String expectedJson = getClassPathResource("/map.json");
		assertEquals(expectedJson, json);
	}

	String getClassPathResource(String name) {
		try (InputStream in = getClass().getResourceAsStream(name)){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int i;
			while (-1 != (i = in.read()))
				baos.write(i);
			return new String(baos.toByteArray(), "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
