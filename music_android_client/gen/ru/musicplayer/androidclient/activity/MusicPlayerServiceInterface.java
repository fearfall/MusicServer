/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/kate/au/3_semestr/music_server/MusicServer/music_android_client/java/src/ru/musicplayer/androidclient/activity/MusicPlayerServiceInterface.aidl
 */
package ru.musicplayer.androidclient.activity;
/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 11/2/11
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MusicPlayerServiceInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements ru.musicplayer.androidclient.activity.MusicPlayerServiceInterface
{
private static final java.lang.String DESCRIPTOR = "ru.musicplayer.androidclient.activity.MusicPlayerServiceInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an ru.musicplayer.androidclient.activity.MusicPlayerServiceInterface interface,
 * generating a proxy if needed.
 */
public static ru.musicplayer.androidclient.activity.MusicPlayerServiceInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof ru.musicplayer.androidclient.activity.MusicPlayerServiceInterface))) {
return ((ru.musicplayer.androidclient.activity.MusicPlayerServiceInterface)iin);
}
return new ru.musicplayer.androidclient.activity.MusicPlayerServiceInterface.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_pause:
{
data.enforceInterface(DESCRIPTOR);
this.pause();
reply.writeNoException();
return true;
}
case TRANSACTION_stop:
{
data.enforceInterface(DESCRIPTOR);
this.stop();
reply.writeNoException();
return true;
}
case TRANSACTION_resume:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.resume(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_play:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
boolean _result = this.play(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getPlayingTrackId:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getPlayingTrackId();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_isPlaying:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.isPlaying(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isPlayingMode:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isPlayingMode();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_seekTo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.seekTo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getCurrentPlayPosition:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurrentPlayPosition();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements ru.musicplayer.androidclient.activity.MusicPlayerServiceInterface
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void pause() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_pause, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void stop() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void resume(java.lang.String trackName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(trackName);
mRemote.transact(Stub.TRANSACTION_resume, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean play(java.lang.String trackName, java.lang.String trackUrl, java.lang.String trackId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(trackName);
_data.writeString(trackUrl);
_data.writeString(trackId);
mRemote.transact(Stub.TRANSACTION_play, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getPlayingTrackId() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPlayingTrackId, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isPlaying(java.lang.String trackMbid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(trackMbid);
mRemote.transact(Stub.TRANSACTION_isPlaying, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isPlayingMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isPlayingMode, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void seekTo(int playPositionInMilliseconds) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(playPositionInMilliseconds);
mRemote.transact(Stub.TRANSACTION_seekTo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getCurrentPlayPosition() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentPlayPosition, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_pause = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_stop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_resume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_play = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getPlayingTrackId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_isPlaying = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_isPlayingMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_seekTo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getCurrentPlayPosition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
}
public void pause() throws android.os.RemoteException;
public void stop() throws android.os.RemoteException;
public void resume(java.lang.String trackName) throws android.os.RemoteException;
public boolean play(java.lang.String trackName, java.lang.String trackUrl, java.lang.String trackId) throws android.os.RemoteException;
public java.lang.String getPlayingTrackId() throws android.os.RemoteException;
public boolean isPlaying(java.lang.String trackMbid) throws android.os.RemoteException;
public boolean isPlayingMode() throws android.os.RemoteException;
public void seekTo(int playPositionInMilliseconds) throws android.os.RemoteException;
public int getCurrentPlayPosition() throws android.os.RemoteException;
}
