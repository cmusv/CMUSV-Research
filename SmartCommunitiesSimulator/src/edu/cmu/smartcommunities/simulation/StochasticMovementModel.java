package edu.cmu.smartcommunities.simulation;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
//import java.io.Serializable;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.Random;




public class StochasticMovementModel
{

	private float[]	 			ma_f__arrival, ma_f__departure;								// [ 0 = mean / 1 = variance ] - mean is time after midnight in minutes and variance is in minutes
	private float[][] 			maa_f__time_at_location;									// [ Location ][ 0 = mean / 1 = variance ]
	private float[][] 			maa_f__bigram;												// [ Last location ][ Next location ]
	private float[][][]			maaa_f__time_to_next_transition;							// [ Last location ][ Next location ][ 0 = mean / 1 = variance ]
	
	
// private Date				m_date__work_start, m_date__work_end;
	private Calendar        m_date__work_start, m_date__work_end;
// private Date				m_date__curr_activity_start;
	private Calendar        m_date__curr_activity_start;
	private int					m_i__curr_activity_duration;
	private WorkdayActivity		m_workdayAcitvity__curr;
	
	
	
	public StochasticMovementModel( String s__in_model_file )
	{
		this.init();
		
		this.loadModel( s__in_model_file );
	}


	public void initDay()
	{
	//	this.initDay( new Date() );
	   this.initDay( Calendar.getInstance() );
		return;
	}
	
	
	
// public void initDay( Date date__in )
	public void initDay( Calendar date__in )
	{
		int		i__t_start_mins, i__t_end_mins;
		
		
	//	this.m_date__work_start = new Date( date__in.getTime() );
		this.m_date__work_start = (Calendar) date__in.clone();
	// this.m_date__work_end = new Date( date__in.getTime() );
		this.m_date__work_end = (Calendar) date__in.clone();
		do
		{
			i__t_start_mins = ( int ) this.sampleGaussianDistribution( this.ma_f__arrival[ 0 ], this.ma_f__arrival[ 1 ] );
			i__t_end_mins = ( int ) this.sampleGaussianDistribution( this.ma_f__departure[ 0 ], this.ma_f__departure[ 1 ] );			
		}
		while(	( i__t_start_mins < 0 ) || 
				( i__t_start_mins >= i__t_end_mins ) || 
				( i__t_end_mins >= 24 * 60 )			);
				
		
	//	m_date__work_start.setHours( i__t_start_mins / 60 );
		m_date__work_start.set(Calendar.HOUR_OF_DAY, i__t_start_mins / 60);
	// m_date__work_start.setMinutes( i__t_start_mins % 60 );
		m_date__work_start.set(Calendar.MINUTE, i__t_start_mins % 60);
	// m_date__work_end.setHours( i__t_end_mins / 60 );
		m_date__work_end.set(Calendar.HOUR_OF_DAY, i__t_end_mins / 60);
	// m_date__work_end.setMinutes( i__t_end_mins % 60 );
		m_date__work_end.set(Calendar.MINUTE, i__t_end_mins % 60);

		
		m_workdayAcitvity__curr = WorkdayActivity.UNKNOWN;
		return;
	}
	
	
	
	
	public void nextActivity()
	{
		Calendar			calendar__t1;
		Date				date__t1;
		WorkdayActivity		workdayActivity__next;
		double				d__t1, d__t2;
	//	int					i__hrs, i__mins;
		int					i__time_at_next_loc, i__time_to_next_loc;
		int					i__c1;
		
		
		if( m_workdayAcitvity__curr == WorkdayActivity.UNKNOWN )
		{
			this.m_workdayAcitvity__curr = WorkdayActivity.ARRIVAL;
			this.m_date__curr_activity_start = this.m_date__work_start;
			this.m_i__curr_activity_duration = -1;
		}
		else if( this.m_workdayAcitvity__curr == WorkdayActivity.DEPARTURE )
			return;
		else
		{
			d__t1 = Math.random();
			d__t2 = 0.0;
			workdayActivity__next = WorkdayActivity.UNKNOWN;
			for( i__c1 = 0; i__c1 < WorkdayActivity.values().length; i__c1++ )
			{
				d__t2 += maa_f__bigram[ m_workdayAcitvity__curr.ordinal() ][ WorkdayActivity.values()[ i__c1 ].ordinal() ];
				if( d__t2 > d__t1 )
				{
					workdayActivity__next = WorkdayActivity.values()[ i__c1 ];
					break;
				}
			}
			if( workdayActivity__next == WorkdayActivity.UNKNOWN ||
				workdayActivity__next == WorkdayActivity.DEPARTURE ||
				workdayActivity__next == WorkdayActivity.ARRIVAL ||
				workdayActivity__next == WorkdayActivity.VISIT_OTHER_WORK_AREA		)
			{
				this.m_workdayAcitvity__curr = WorkdayActivity.DEPARTURE;
				this.m_date__curr_activity_start = this.m_date__work_end;
				this.m_i__curr_activity_duration = -1;
				return;			
			}
			
			
			
		// calendar__t1 = Calendar.getInstance();
		// calendar__t1.setTime( this.m_date__curr_activity_start );
			calendar__t1 = (Calendar) this.m_date__curr_activity_start.clone();
			if( this.m_i__curr_activity_duration > 0 )
				calendar__t1.add( Calendar.MINUTE, this.m_i__curr_activity_duration );

			i__time_to_next_loc = -1;
			i__time_at_next_loc = -1;
			//System.out.println( " -- " + workdayActivity__next );
			while( i__time_at_next_loc <=0 || i__time_to_next_loc <= 0 )
			{
				i__time_to_next_loc = ( int ) sampleGaussianDistribution( 	maaa_f__time_to_next_transition[ this.m_workdayAcitvity__curr.ordinal() ][ workdayActivity__next.ordinal() ][ 0 ],
																			maaa_f__time_to_next_transition[ this.m_workdayAcitvity__curr.ordinal() ][ workdayActivity__next.ordinal() ][ 1 ]	);
				i__time_at_next_loc = ( int ) sampleGaussianDistribution( 	maa_f__time_at_location[ workdayActivity__next.ordinal() ][ 0 ],
																			maa_f__time_at_location[ workdayActivity__next.ordinal() ][ 1 ] 	);
			}

			calendar__t1.add( Calendar.MINUTE, i__time_to_next_loc );
			date__t1 = calendar__t1.getTime();
			calendar__t1.add( Calendar.MINUTE, i__time_at_next_loc );
			
			if( this.m_date__work_end.before( calendar__t1.getTime() ) )
			{
				this.m_workdayAcitvity__curr = WorkdayActivity.DEPARTURE;
				this.m_date__curr_activity_start = this.m_date__work_end;
				this.m_i__curr_activity_duration = -1;
				return;
			}
			
			this.m_workdayAcitvity__curr = workdayActivity__next;
		// this.m_date__curr_activity_start = date__t1;
			this.m_date__curr_activity_start.setTime(date__t1);
			this.m_i__curr_activity_duration = i__time_at_next_loc;
		}
		
		return;
	}
	




// public int setCurrentActivity( WorkdayActivity workdayActivity__in, Date date__in_startTime, int i__in_duration )
   public int setCurrentActivity( WorkdayActivity workdayActivity__in, Calendar date__in_startTime, int i__in_duration )
	{
		Calendar		calendar__t1;
		
		
	// calendar__t1 = Calendar.getInstance();
	// calendar__t1.setTime( date__in_startTime );
		calendar__t1 = (Calendar) date__in_startTime.clone();
		if( i__in_duration > 0 )
			calendar__t1.add( Calendar.MINUTE, i__in_duration );

		if( this.m_date__work_end.before( calendar__t1.getTime() ) )						// check if the activity runs past the end of the workday (i.e., the clock out time for that day)
			return -1;
		else
		{
			this.m_workdayAcitvity__curr = workdayActivity__in;
			this.m_date__curr_activity_start = date__in_startTime;
			this.m_i__curr_activity_duration = i__in_duration;
		}
		
		return 0;
	}
	
	
	
	
	public WorkdayActivity getActivity()
	{
		return this.m_workdayAcitvity__curr;
	}
// public Date getActvityStartTime()
	public Calendar getActivityStartTime()
	{
		return this.m_date__curr_activity_start;
	}
	public int getActivityDuration()
	{
		return this.m_i__curr_activity_duration;
	}
	
	
// public Date getWorkStartTime()
	public Calendar getWorkStartTime()
	{
		return this.m_date__work_start;
	}
// public Date getWorkEndTime()
	public Calendar getWorkEndTime()
	{
		return this.m_date__work_end;
	}

	
	
	
	
	
	
	
	
	
	private float sampleGaussianDistribution( float f__mean, float f__var )
	{
		double		d__cpd;
		double		d__x;

		
		d__cpd = Math.random();

		d__x = inverseErrorFunc( ( 2 * d__cpd ) - 1 );
		d__x *= Math.sqrt( Math.PI * f__var );
		d__x += f__mean;
		return ( float ) d__x;
	}
	
	
	
	
	
	private double inverseErrorFunc( double d__x )
	{
		double		d__a, d__t1, d__sign;
		double		d__out;

		
		if( d__x == 0 )
			d__sign = 0;
		else if( d__x < 0 )
			d__sign = -1;
		else
			d__sign = 1;
			
		d__t1 = 1 - ( d__x * d__x );
		
		d__a = ( 8 * ( Math.PI - 3 ) ) / ( 3 * Math.PI * ( 4 - Math.PI ) );
		
		d__out = ( 2 / ( Math.PI * d__a ) ) + ( Math.log( d__t1 ) / 2 );
		d__out *= d__out;
		d__out -= Math.log( d__t1 ) / d__a;
		d__out = Math.sqrt( d__out );
		d__out -= ( 2 / ( Math.PI * d__a ) ) + ( Math.log( d__t1 ) / 2 );
		d__out = Math.sqrt( d__out );
		
		d__out *= d__sign;	
		return d__out;
	}
	
	
	
	
	
	
	
	
	private void init()
	{
		int			i__x, i__y;
		
		
		this.ma_f__arrival = new float[ 2 ];
		this.ma_f__departure = new float[ 2 ];
		this.maa_f__time_at_location = new float[ WorkdayActivity.values().length ][ 2 ];
		this.maa_f__bigram = new float[ WorkdayActivity.values().length ][ WorkdayActivity.values().length ];
		for( i__y = 0; i__y < WorkdayActivity.values().length; i__y++ )
			for( i__x = 0; i__x < WorkdayActivity.values().length; i__x++ )
				this.maa_f__bigram[ i__y ][ i__x ] = 0.0f;
		this.maaa_f__time_to_next_transition = new float[ WorkdayActivity.values().length ][ WorkdayActivity.values().length ][ 2 ];
		return;
	}
	
	
	
	
	
	
	
	
	private void loadModel( String s__in_model_file )
	{
		BufferedReader 			bufferedReader__ip;
		String					s__line;
		String[]				a_s__fields;
		WorkdayActivity			workdayActivity__curr, workdayActivity__next;


		
		try
		{
			bufferedReader__ip =  new BufferedReader( new FileReader( new File( s__in_model_file )  ) );
			try
			{
				a_s__fields = bufferedReader__ip.readLine().split( "," );				// Load arrival parameters
				this.ma_f__arrival[ 0 ] = Float.parseFloat( a_s__fields[ 1 ] );
				this.ma_f__arrival[ 1 ] = Float.parseFloat( a_s__fields[ 2 ] );
				
				a_s__fields = bufferedReader__ip.readLine().split( "," );				// Load departure parameters
				this.ma_f__departure[ 0 ] = Float.parseFloat( a_s__fields[ 1 ] );
				this.ma_f__departure[ 1 ] = Float.parseFloat( a_s__fields[ 2 ] );


				while( ( s__line = bufferedReader__ip.readLine()) != null )
				{
					a_s__fields = s__line.split( "," );
					if( a_s__fields.length == 3 )										// Time spent at a location
					{
						workdayActivity__curr = getWorkdayActivity( a_s__fields[ 0 ] );
						this.maa_f__time_at_location[ workdayActivity__curr.ordinal() ][ 0 ] = Float.parseFloat( a_s__fields[ 1 ] );
						this.maa_f__time_at_location[ workdayActivity__curr.ordinal() ][ 1 ] = Float.parseFloat( a_s__fields[ 2 ] );
					}
					else																// Probability and delay of transitioning to a location
					{
						workdayActivity__curr = getWorkdayActivity( a_s__fields[ 0 ] );
						workdayActivity__next = getWorkdayActivity( a_s__fields[ 1 ] );
						this.maa_f__bigram[ workdayActivity__curr.ordinal() ][ workdayActivity__next.ordinal() ] = Float.parseFloat( a_s__fields[ 2 ] );
						this.maaa_f__time_to_next_transition[ workdayActivity__curr.ordinal() ][ workdayActivity__next.ordinal() ][ 0 ] = Float.parseFloat( a_s__fields[ 3 ] );
						this.maaa_f__time_to_next_transition[ workdayActivity__curr.ordinal() ][ workdayActivity__next.ordinal() ][ 1 ] = Float.parseFloat( a_s__fields[ 4 ] );					
					}
				}
			}
			catch( Exception exception__t1 )
			{
				exception__t1.printStackTrace();
			}			
			finally
			{
				bufferedReader__ip.close();
			}
		}
		catch( IOException ioException__t1 )
		{
			ioException__t1.printStackTrace();
		}
		return;
	}
	
	
	
	
	
	
	

	
	private WorkdayActivity getWorkdayActivity( String s__in )
	{
		if( s__in.equals( "B1" ) )
			return WorkdayActivity.BREAK;
		if( s__in.equals( "B2" ) )
			return WorkdayActivity.RESTROOM;
		if( s__in.equals( "B3" ) )
			return WorkdayActivity.BREAK_AND_RESTROOM;
		if( s__in.equals( "L" ) )
			return WorkdayActivity.LEAVE_BUILDING;
		if( s__in.equals( "M" ) )
			return WorkdayActivity.MEETING;
		if( s__in.equals( "V1" ) )
			return WorkdayActivity.VISIT_OTHER_WORK_AREA;
		if( s__in.equals( "A" ) )
			return WorkdayActivity.ARRIVAL;
		if( s__in.equals( "D" ) )
			return WorkdayActivity.DEPARTURE;
			
		return WorkdayActivity.UNKNOWN;
	}

	
}
