## Setup

Declare features and permissions needed in manifest.

```
<manifest ...>
	...	
	<uses-feature android:name="android.hardware.camera"
        	android:required="false" />

	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        	android:maxSdkVersion="18" />
	...
</manifest>
```

Declare provider in manifest within `<application>` tags.

```
<application ...>
	...
	<provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="com.georgeneokq.dressup.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths"></meta-data>
        </provider>
</application>

```