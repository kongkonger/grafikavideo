/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.grafika;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.view.Surface;

import com.android.grafika.encoder.MediaMuxerWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * This class wraps up the core components used for surface-input video encoding.
 * <p>
 * Once created, frames are fed to the input surface.  Remember to provide the presentation
 * time stamp, and always call drainEncoder() before swapBuffers() to ensure that the
 * producer side doesn't get backed up.
 * <p>
 * This class is not thread-safe, with one exception: it is valid to use the input surface
 * on one thread, and drain the output on a different thread.
 */
public class VideoEncoderCore {
    private static final String TAG = MainActivity.TAG;
    private static final boolean VERBOSE = false;

    // TODO: these ought to be configurable as well
    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 30;               // 30fps
    private static final int IFRAME_INTERVAL = 5;           // 5 seconds between I-frames
    private MediaMuxerWrapper mMuxer;
    private Surface mInputSurface;
//    private MediaMuxer mMuxer;
    private MediaCodec mEncoder;
    private MediaCodec.BufferInfo mBufferInfo;
    private int mTrackIndex;
    private boolean mMuxerStarted;


    /**
     * Configures encoder and muxer state, and prepares the input Surface.
     */
//    public VideoEncoderCore(int width, int height, int bitRate, File outputFile)
//            throws IOException {
//        mBufferInfo = new MediaCodec.BufferInfo();
//
//        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, width, height);
//
//        // Set some properties.  Failing to specify some of these can cause the MediaCodec
//        // configure() call to throw an unhelpful exception.
//        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
//                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
//        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
//        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
//        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
//        if (VERBOSE) Log.d(TAG, "format: " + format);
//
//        // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
//        // we can use for input and wrap it with a class that handles the EGL work.
//        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
//        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//        mInputSurface = mEncoder.createInputSurface();
//        mEncoder.start();
//
//        // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
//        // because our MediaFormat doesn't have the Magic Goodies.  These can only be
//        // obtained from the encoder after it has started processing data.
//        //
//        // We're not actually interested in multiplexing audio.  We just want to convert
//        // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.
//        // TODO: 2020/5/7 origin code
////        mMuxer = new MediaMuxer(outputFile.toString(),
////                MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
//
//        // TODO: 2020/5/7 cmc add
//        mMuxer = new MediaMuxerWrapper(".mp4");
//        mTrackIndex = -1;
//        mMuxerStarted = false;
//    }


    public VideoEncoderCore(int width, int height, int bitRate, File outputFile)
            throws IOException {
        mBufferInfo = new MediaCodec.BufferInfo();

        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, width, height);

        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        if (VERBOSE) Log.d(TAG, "format: " + format);

        // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
        // we can use for input and wrap it with a class that handles the EGL work.
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mInputSurface = mEncoder.createInputSurface();
        mEncoder.start();

        // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
        // because our MediaFormat doesn't have the Magic Goodies.  These can only be
        // obtained from the encoder after it has started processing data.
        //
        // We're not actually interested in multiplexing audio.  We just want to convert
        // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.
        // TODO: 2020/5/7 origin code
//        mMuxer = new MediaMuxer(outputFile.toString(),
//                MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

//        final MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
//        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);	// API >= 18
//        format.setInteger(MediaFormat.KEY_BIT_RATE, calcBitRate());
//        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
//        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10);
//        if (DEBUG) Log.i(TAG, "format: " + format);
//
//        mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
//        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//        // get Surface for encoder input
//        // this method only can call between #configure and #start
//        mSurface = mMediaCodec.createInputSurface();	// API >= 18
//        mMediaCodec.start();



        // TODO: 2020/5/7 cmc add
        mMuxer = MediaMuxerWrapper.getInstance();
        mTrackIndex = -1;
        mMuxerStarted = false;
    }
    /**
     * Returns the encoder's input surface.
     */
    public Surface getInputSurface() {
        return mInputSurface;
    }

    /**
     * Releases encoder resources.
     */
    public void release() {
        if (VERBOSE) Log.d(TAG, "releasing encoder objects");
        mIsCapturing = false;
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mMuxer != null) {
            // TODO: stop() throws an exception if you haven't fed it any data.  Keep track
            //       of frames submitted, and don't call stop() if we haven't written anything.
//            mMuxer.stop();
//            mMuxer.release();
            mMuxer.stopRecording();

            mMuxer = null;
        }

    }

    /**
     * Extracts all pending data from the encoder and forwards it to the muxer.
     * <p>
     * If endOfStream is not set, this returns when there is no more data to drain.  If it
     * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
     * Calling this with endOfStream set should be done once, right before stopping the muxer.
     * <p>
     * We're just using the muxer to get a .mp4 file (instead of a raw H.264 stream).  We're
     * not recording audio.
     */
    public void drainEncoder(boolean endOfStream) {
        final int TIMEOUT_USEC = 10000;
        if (VERBOSE) Log.d(TAG, "drainEncoder(" + endOfStream + ")");

        if (endOfStream) {
            if (VERBOSE) Log.d(TAG, "sending EOS to encoder");
//
            mEncoder.signalEndOfInputStream();
        }

        ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();
        LOOP:  while (true) {
            int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break;      // out of while
                } else {
                    if (VERBOSE) Log.d(TAG, "no output available, spinning to await EOS");
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = mEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (mMuxerStarted) {
                    throw new RuntimeException("format changed twice");
                }
                // TODO: 2020/5/7 origin
//                MediaFormat newFormat = mEncoder.getOutputFormat();
//                Log.d(TAG, "encoder output format changed: " + newFormat);
//
//                // now that we have the Magic Goodies, start the muxer
//                mTrackIndex = mMuxer.addTrack(newFormat);
//                mMuxer.start();
//                mMuxerStarted = true;
                // TODO: 2020/5/7 cmc add
                final MediaFormat format = mEncoder.getOutputFormat(); // API >= 16
                mTrackIndex = mMuxer.addTrack(format);
                mMuxerStarted = true;
                if (!mMuxer.start()) {
                    // we should wait until muxer is ready`
                    synchronized (mMuxer) {
                        while (!mMuxer.isStarted())
                            try {
                                mMuxer.wait(100);
                            } catch (final InterruptedException e) {
                                break LOOP;
                            }
                    }
                }

            } else if (encoderStatus < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                        encoderStatus);
                // let's ignore it
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                            " was null");
                }

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw new RuntimeException("muxer hasn't started");
                    }

                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

//                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
//                    if (VERBOSE) {
//                        Log.d(TAG, "sent " + mBufferInfo.size + " bytes to muxer, ts=" +
//                                mBufferInfo.presentationTimeUs);
//                    }

                    mBufferInfo.presentationTimeUs = getPTSUs();
                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                    prevOutputPTSUs = mBufferInfo.presentationTimeUs;
                }

                mEncoder.releaseOutputBuffer(encoderStatus, false);

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.w(TAG, "reached end of stream unexpectedly");
                    } else {
                        if (VERBOSE) Log.d(TAG, "end of stream reached");
                    }
                    break;      // out of while
                }
            }
        }
    }






    protected void signalEndOfInputStream() {
        if (DEBUG) Log.d(TAG, "sending EOS to encoder");
        // signalEndOfInputStream is only avairable for video encoding with surface
        // and equivalent sending a empty buffer with BUFFER_FLAG_END_OF_STREAM flag.
//		mMediaCodec.signalEndOfInputStream();	// API >= 18
        encode(null, 0, getPTSUs());
    }

    /**
     * Method to set byte array to the MediaCodec encoder
     * @param buffer
     * @param length　length of byte array, zero means EOS.
     * @param presentationTimeUs
     */
    protected void encode(final ByteBuffer buffer, final int length, final long presentationTimeUs) {
        if (!mIsCapturing) return;
        final ByteBuffer[] inputBuffers = mEncoder.getInputBuffers();
        while (mIsCapturing) {
            final int inputBufferIndex = mEncoder.dequeueInputBuffer(TIMEOUT_USEC);
            if (inputBufferIndex >= 0) {
                final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                if (buffer != null) {
                    inputBuffer.put(buffer);
                }
//	            if (DEBUG) Log.v(TAG, "encode:queueInputBuffer");
                if (length <= 0) {
                    // send EOS
                    mIsEOS = true;
                    if (DEBUG) Log.i(TAG, "send BUFFER_FLAG_END_OF_STREAM");
                    mEncoder.queueInputBuffer(inputBufferIndex, 0, 0,
                            presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    break;
                } else {
                    mEncoder.queueInputBuffer(inputBufferIndex, 0, length,
                            presentationTimeUs, 0);
                }
                break;
            } else if (inputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // wait for MediaCodec encoder is ready to encode
                // nothing to do here because MediaCodec#dequeueInputBuffer(TIMEOUT_USEC)
                // will wait for maximum TIMEOUT_USEC(10msec) on each call
            }
        }
    }

    /**
     * drain encoded data and write them to muxer
     */
//    protected void drainEncoder(boolean endOfStream) {
//        if (mEncoder == null) return;
//        if (endOfStream) {
//            if (VERBOSE) Log.d(TAG, "sending EOS to encoder");
//            signalEndOfInputStream();
//        }
//        ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();
//        int encoderStatus, count = 0;
//        final MediaMuxerWrapper muxer = MediaMuxerWrapper.getInstance();
//        if (muxer == null) {
////        	throw new NullPointerException("muxer is unexpectedly null");
//            Log.w(TAG, "muxer is unexpectedly null");
//            return;
//        }
//        LOOP:	while (mIsCapturing) {
//            // get encoded data with maximum timeout duration of TIMEOUT_USEC(=10[msec])
//            encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
//            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
//                // wait 5 counts(=TIMEOUT_USEC x 5 = 50msec) until data/EOS come
//                if (!mIsEOS) {
//                    if (++count > 5)
//                        break LOOP;		// out of while
//                }
//            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//                if (DEBUG) Log.v(TAG, "INFO_OUTPUT_BUFFERS_CHANGED");
//                // this shoud not come when encoding
//                encoderOutputBuffers = mEncoder.getOutputBuffers();
//            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                if (DEBUG) Log.v(TAG, "INFO_OUTPUT_FORMAT_CHANGED");
//                // this status indicate the output format of codec is changed
//                // this should come only once before actual encoded data
//                // but this status never come on Android4.3 or less
//                // and in that case, you should treat when MediaCodec.BUFFER_FLAG_CODEC_CONFIG come.
//                if (mMuxerStarted) {	// second time request is error
//                    throw new RuntimeException("format changed twice");
//                }
//                // get output format from codec and pass them to muxer
//                // getOutputFormat should be called after INFO_OUTPUT_FORMAT_CHANGED otherwise crash.
//                final MediaFormat format = mEncoder.getOutputFormat(); // API >= 16
//                mTrackIndex = muxer.addTrack(format);
//                mMuxerStarted = true;
//                if (!muxer.start()) {
//                    // we should wait until muxer is ready
//                    synchronized (muxer) {
//                        while (!muxer.isStarted())
//                            try {
//                                muxer.wait(100);
//                            } catch (final InterruptedException e) {
//                                break LOOP;
//                            }
//                    }
//                }
//            } else if (encoderStatus < 0) {
//                // unexpected status
//                if (DEBUG) Log.w(TAG, "drain:unexpected result from encoder#dequeueOutputBuffer: " + encoderStatus);
//            } else {
//                final ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
//                if (encodedData == null) {
//                    // this never should come...may be a MediaCodec internal error
//                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
//                }
//                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
//                    // You shoud set output format to muxer here when you target Android4.3 or less
//                    // but MediaCodec#getOutputFormat can not call here(because INFO_OUTPUT_FORMAT_CHANGED don't come yet)
//                    // therefor we should expand and prepare output format from buffer data.
//                    // This sample is for API>=18(>=Android 4.3), just ignore this flag here
//                    if (DEBUG) Log.d(TAG, "drain:BUFFER_FLAG_CODEC_CONFIG");
//                    mBufferInfo.size = 0;
//                }
//
//                if (mBufferInfo.size != 0) {
//                    // encoded data is ready, clear waiting counter
//                    count = 0;
//                    if (!mMuxerStarted) {
//                        // muxer is not ready...this will prrograming failure.
//                        throw new RuntimeException("drain:muxer hasn't started");
//                    }
//                    // write encoded data to muxer(need to adjust presentationTimeUs.
//                    mBufferInfo.presentationTimeUs = getPTSUs();
//                    muxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
//                    prevOutputPTSUs = mBufferInfo.presentationTimeUs;
//                }
//                // return buffer to encoder
//                mEncoder.releaseOutputBuffer(encoderStatus, false);
//                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
//                    // when EOS come.
//                    mIsCapturing = false;
//                    break;      // out of while
//                }
//            }
//        }
//    }





    protected final Object mSync = new Object();

    public void setIsCapturing(boolean mIsCapturing) {
        this.mIsCapturing = mIsCapturing;
    }

    /**
     * Flag that indicate this encoder is capturing now.
     */
    protected volatile boolean mIsCapturing;
    /**
     * Flag that indicate the frame data will be available soon.
     */
    private int mRequestDrain;
    /**
     * Flag to request stop capturing
     */
    protected volatile boolean mRequestStop;
    /**
     * Flag that indicate encoder received EOS(End Of Stream)
     */
    protected boolean mIsEOS;
    private static final boolean DEBUG = false;	// TODO set false on release
//    private static final String TAG = "MediaEncoder";

    protected static final int TIMEOUT_USEC = 10000;	// 10[msec]
    /**
     * previous presentationTimeUs for writing
     */
    private long prevOutputPTSUs = 0;
    /**
     * get next encoding presentationTimeUs
     * @return
     */
    protected long getPTSUs() {
        long result = System.nanoTime() / 1000L;
        // presentationTimeUs should be monotonic
        // otherwise muxer fail to write
        if (result < prevOutputPTSUs)
            result = (prevOutputPTSUs - result) + result;
        return result;
    }
}
