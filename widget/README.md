# WIDGET
#### 앱의 위젯부분과 JobService 를 이용한 위젯컨트롤이 가능하다
##### Android O 부터는 Background Service에 제한이 있어 JobService 를 사용하도록 한다.

* res 폴더에 있는 것들은 포함하지않았음
* java 및 xml 파일들 간략설명
   * LMWidget.java : 위젯 컨트롤 부분이다.
   * LMJobScheduler.java : JobService 를 구동하기위한 조건
   * SyncJobService.java : 원하고자하는 Service 를 구동시킨다.
   * xml/lm_widget_provider.xml : widget 의 provider로 widget update 주기 또는 사이즈등 설정을 할 수 있다.
   
* 이 외에 필요한 것들은

Widget 의 receiver 등록 및 setting, meta-data 등록
<pre>
<code>
<receiver android:name=".widget.LMWidget">
	<intent-filter>
		<action android:name="android.intent.action.BOOT_COMPLETED"/>
		<action android:name="android.appwidget.action.APPWIDGET_UPDATE"/>
		<action android:name="appwidget.broadcast.check"/> //WIDGET_BROADCAST_CHECK
	</intent-filter>

	<meta-data
		android:name="android.appwidget.provider"
		android:resource="@xml/lm_widget_provider"/>
		
</receiver>
</code>
</pre>

JobService 등록
<pre>
<code>
<service android:name=".SyncJobService"
	 android:permission="android.permission.BIND_JOB_SERVICE"
	 android:exported="true"/>
</receiver>
</code>
</pre>