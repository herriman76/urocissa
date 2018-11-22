package com.sanyinggroup.corp.urocissa.client.api.future;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
/**
 * <p>Package:com.sanyinggroup.corp.urocissa.client.api.future</p> 
 * <p>Title:SyncSendFuture</p> 
 * <p>Description: </p> 
 * @author lixiao
 * @date 2017年8月1日 下午4:11:02
 * @version
 */
public class SyncSendFuture implements SendFuture<MiddleMsg>{
	
	private CountDownLatch latch = new CountDownLatch(1);
    private final long begin = System.currentTimeMillis();
    private long timeout;
    private MiddleMsg response;
    private final String requestId;
    private boolean writeResult;
    private Throwable cause;
    private boolean isTimeout = false;
    
    public SyncSendFuture(String requestId) {
		super();
		this.requestId = requestId;
		timeout  = 30*1000L; // 默认10秒
	}
	
	public SyncSendFuture( long timeout, String requestId) {
		super();
		this.timeout = timeout;
		this.requestId = requestId;
		this.writeResult = true;
        this.isTimeout = false;
	}
    
	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}
	/**
	 * @return the cause
	 */
	public Throwable getCause() {
		return cause;
	}
	/**
	 * @param isTimeout the isTimeout to set
	 */
	public void setTimeout(boolean isTimeout) {
		this.isTimeout = isTimeout;
	}
	
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public MiddleMsg get() throws InterruptedException, ExecutionException {
		latch.await();
		return  response;
	}

	@Override
	public MiddleMsg get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		if (latch.await(timeout, unit)) {
            return response;
        }
		return null;
	}

	@Override
	public Throwable cause() {
		return cause;
	}

	@Override
	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	@Override
	public boolean isWriteSuccess() {
		return writeResult;
	}

	@Override
	public void setWriteResult(boolean result) {
		this.writeResult = result;
	}

	@Override
	public String requestId() {
		return requestId;
	}

	@Override
	public MiddleMsg getResponse() {
		try {
			latch.await(timeout,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public void setResponse(MiddleMsg response) {
		this.response = response;
		latch.countDown();
	}

	@Override
	public boolean isTimeout() {
		 if (isTimeout) {
	            return isTimeout;
	        }
	     return System.currentTimeMillis() - begin > timeout;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SyncSendFuture [begin=" + begin
				+ ", timeout=" + timeout + ", response=" + response
				+ ", requestId=" + requestId + ", writeResult=" + writeResult
				+ ", cause=" + cause + ", isTimeout=" + isTimeout + "]";
	}
	
}
