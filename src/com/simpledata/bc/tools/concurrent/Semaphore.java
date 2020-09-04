/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */


package com.simpledata.bc.tools.concurrent; 

import java.util.Date;

/**
 * Semaphore as documented in the java 1.5 draft. This 
 * can be removed once we compile with java 1.5, replace
 * it with java.util.concurrent.Semaphore. 
 *
 * Note that all settings in relation to fairness are 
 * ignored. This implementation is more or less fair,  
 * in the sense that not only a few threads should be 
 * obtaining the resources, but that all threads should 
 * get their turn. 
 * 
 * @version $Id: Semaphore.java,v 1.2 2007/04/02 17:04:31 perki Exp $ 
 * @author Simpledata SARL, 2004, all rights reserved. 
 */
public class Semaphore {
	private int m_permits = 0;

	/**
	 * Creates a Semaphore with the given number of 
	 * permits and nonfair fairness setting.
	 * 
	 * @param permits the initial number of permits available. 
	 *                This value may be negative, in which case 
	 *                releases must occur before any acquires will be granted.
	 */
	public Semaphore(int permits) {
		m_permits = permits;
	}
       
	/**
	 * Creates a Semaphore with the given number of 
	 * permits and the given fairness setting.
	 * @param fair true if this semaphore will guarantee first-in 
	 *             first-out granting of permits under contention. 
	 *             <b> NOT IMPLEMENTED HERE ! SEE COMMENT FOR CLASS </b>
	 * @param permits the initial number of permits available. This value may be 
	 *                negative, in which case releases must occur before any acquires 
	 *                will be granted.
	 */
	public Semaphore(int permits, boolean fair) {
		m_permits = permits;
	}
	
	/**
	 * Acquires a permit from this semaphore, blocking until one 
	 * is available, or the thread is interrupted.
	 * 
	 * Acquires a permit, if one is available and returns immediately, 
	 * reducing the number of available permits by one.
	 * 
	 * If no permit is available then the current thread becomes 
	 * disabled for thread scheduling purposes and lies dormant 
	 * until one of two things happens:
	 * <ul>
	 *  <li>Some other thread invokes the release() method for this 
	 *     semaphore and the current thread is next to be assigned 
	 *     a permit; or </li>
	 *  <li>Some other thread interrupts the current thread. </li>
	 * </ul>
	 *
	 * If the current thread:
   * <ul>
	 *  <li>has its interrupted status set on entry to this method; or</li>
	 *  <li>is interrupted while waiting for a permit, </li>
   * </ul>
	 * then InterruptedException is thrown and the current thread's 
	 * interrupted status is cleared.
	 * 
	 * @throw InterruptedException if the current thread is interrupted
	 */
	public void acquire() throws InterruptedException {
		synchronized (this) {
			// no resources left ? 
			while ( m_permits - 1 < 0 ) {
				this.wait(); 
			}  // no resources left ?
			
			m_permits -= 1;
		}  // synchronized
	}
	
	/**
	 * Returns the current number of permits available in this semaphore.
	 * 
	 * This method is typically used for debugging and testing purposes. 
	 * @return the number of permits available in this semaphore.
	 */
	public int availablePermits() {
		return m_permits;
	}
	
	/** 
	 * Acquire and return all permits that are immediately available.
	 * @return the number of permits. 
	 */ 
	public int drainPermits() {
		synchronized ( this ) {
			if ( m_permits >= 0 ) {
				int acquired = m_permits;
				m_permits = 0; 
				
				return acquired; 
			}
			return 0;
		}
	}
	
	/**
	 * Releases a permit, returning it to the semaphore.
   * 
	 * Releases a permit, increasing the number of available permits 
	 * by one. If any threads are trying to acquire a permit, then 
	 * one is selected and given the permit that was just released. 
	 * That thread is (re)enabled for thread scheduling purposes.
	 * 
	 * There is no requirement that a thread that releases a permit must 
	 * have acquired that permit by calling acquire(). Correct usage 
	 * of a semaphore is established by programming convention in the 
	 * application. 
	 */
	public void release() {
		synchronized ( this ) {
			m_permits += 1; 
			this.notifyAll();
		}
	}

	/**
	 * Acquires a permit from this semaphore, only if one 
	 * is available at the time of invocation.
	 * 
	 * Acquires a permit, if one is available and returns immediately, 
	 * with the value true, reducing the number of available 
	 * permits by one.
	 * 
	 * If no permit is available then this method will return immediately 
	 * with the value false.
	 * 
	 * Even when this semaphore has been set to use a fair ordering policy, 
	 * a call to tryAcquire() will immediately acquire a permit if 
	 * one is available, whether or not other threads are currently waiting. 
	 * This "barging" behavior can be useful in certain circumstances, 
	 * even though it breaks fairness. If you want to honor the fairness 
	 * setting, then use tryAcquire(0, TimeUnit.SECONDS) which is almost 
	 * equivalent (it also detects interruption). 
	 */
	public boolean tryAcquire() {
		synchronized ( this ) {
			if (m_permits > 0) {
				m_permits -= 1; 
				return true; 
			}
			return false; 
		}
	}
	
	/**
	 * Acquires a permit from this semaphore, if one becomes 
	 * available within the given waiting time and the current 
	 * thread has not been interrupted.
	 * 
	 * Acquires a permit, if one is available and returns immediately, 
	 * with the value true, reducing the number of available 
	 * permits by one.
	 * 
	 * If no permit is available then the current thread becomes disabled 
	 * for thread scheduling purposes and lies dormant until one of 
	 * three things happens:
	 * 
	 * <ul>
	 *  <li>Some other thread invokes the release() method for this 
	 *      semaphore and the current thread is next to be assigned a permit; or</li>
	 *  <li>Some other thread interrupts the current thread; or</li>
	 *  <li>The specified waiting time elapses.</li>
	 * </ul>
   * 
	 * If a permit is acquired then the value true is returned.
   * 
	 * If the current thread:
	 * <ul>
   *  <li>has its interrupted status set on entry to this method; or</li>
	 *  <li>is interrupted while waiting to acquire a permit, </li>
	 * </ul>
	 * then InterruptedException is thrown and the current thread's 
	 * interrupted status is cleared. 
	 *
	 * If the specified waiting time elapses then the value false is returned. 
	 * If the time is less than or equal to zero, the method will not wait at all. 
	 * 
	 * @param timeout the maximum time to wait for a permit in milliseconds
	 *
	 * @return true if a permit was acquired and false  
	 *         if the waiting time elapsed before a permit was acquired.
	 * @throws InterruptedException if the current thread is interrupted
	 */
	public boolean tryAcquire( long timeout ) 
		throws InterruptedException {
		synchronized ( this ) {
			if (m_permits > 0) {
				m_permits -= 1; 
				return true; 
			}
			
			// assert: no permits available now...
			Date longestWait = new Date( ( new Date() ).getTime() + timeout );
			boolean breaked = false; 
			
			while ( m_permits <= 0 ) {
				this.wait( longestWait.getTime() - new Date().getTime() );
				
				Date now = new Date(); 
				
				// no permits available and waited for too long ? 
				if ( now.after( longestWait ) && m_permits <= 0 ) {
					return false; 
				}
			}
			
			assert m_permits > 0 : "Permits must be available"; 
			m_permits -= 1;
			return true; 
		}
	}
	
}
