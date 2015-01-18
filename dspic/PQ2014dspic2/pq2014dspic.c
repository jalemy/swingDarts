//
// pq2014dspic.c    dsPIC3012 wristwatch computer
//				    MPU-6050 6-axis motion sensor via I2C I/F
//					+ IEEE 802.15.4 wireless communication support
//					X'tal 3.6864 MHz
//					EIA-232C 115.2Kbps high-speed mode available
//
// Nov.21 2014	Soichiro C. Matsushita
// Group4 revision
//
#include <p30F3012.h>
#include <math.h>

_FOSC(CSW_FSCM_OFF & XTL);
// XTL -> XT 4MHz X'tal mode
_FWDT(WDT_OFF);
_FBORPOR(MCLR_DIS & PBOR_OFF & BORV_27 & PWRT_4);
_FGS(CODE_PROT_OFF & GWRP_OFF);
//_FICD(ICS_NONE);

// Interrupt Routine
// Priority : default
// Priority Level : default
void __attribute__((__interrupt__)) _T1Interrupt(void);
void __attribute__((__interrupt__)) _T2Interrupt(void);
void __attribute__((__interrupt__)) _T3Interrupt(void);
void __attribute__((__interrupt__)) _U1RXInterrupt(void);

extern void transmit(int);			// transmit.s assembly code
extern void receivedetect(void);	// receive detection code

unsigned int timeperiod;
unsigned int timeperiod2;
unsigned int timeperiod3;

void transmitx(unsigned char);
unsigned char receivex(void);

void __attribute__((interrupt, no_auto_psv))_T1Interrupt(void)
{
	IFS0bits.T1IF = 0;
	timeperiod = 1;
}

void __attribute__((interrupt, no_auto_psv))_T2Interrupt(void)
{
	IFS0bits.T2IF = 0;
	timeperiod2 = 1;
}

void __attribute__((interrupt, no_auto_psv))_T3Interrupt(void)
{
	IFS0bits.T3IF = 0;
	timeperiod3 = 1;
}

void __attribute__((interrupt, no_auto_psv))_U1RXInterrupt(void)
{
	IFS0bits.U1RXIF = 0;
	while(U1STAbits.URXDA){
	}
}

// Software time for backward compatibility
//
// Wait1S waits for 1 second
//
void Wait1S(void)
{
// 1.000316 sec @ 3.6864MHz clock
	int	i, j;

	for( i = 0; i < 125; i++){
		for( j = 0; j < 1472; j++){
		}
//		asm("nop");
	}
}

//
// Wait05S waits for 0.5 second
//
void Wait05S(void)
{
// 0.504168 sec @ 3.6864MHz clock
	int i, j;

	for( i = 0; i < 63; i++){
		for( j = 0; j < 1472; j++){
		}
	}
}

//
// Wait5mS waits for 5 milliseconds
//
void Wait5mS(void)
{
// 4.990msec @ 3.6864MHz clock
	int i, j;

	for( i = 0; i < 6; i++){
		for( j = 0; j < 150; j++){
		}
//		asm("nop");
	}
}

//
// Wait1mS waits for 1 millisecond
//
void Wait1mS(void)
{
// 1.005859msec @ 3.6864MHz clock
	int	i, j;

	for( i = 0; i < 3; i++){
		for( j = 0; j < 58; j++){
		}
	}
}

void Wait500uS(void)
{
// 501.3usec @ 3.6864MHz clock
	int i, j;

	for( i = 0; i < 3; i++){
		for( j = 0; j < 27; j++){
		}
	}
}

//
// 12-b analog-digital conversion
//

//
// A/D conversion timing controller
//
void ADconv(void)
{
	ADCON1bits.ADON = 1;	// A/D converter module activated
	ADCON1bits.SAMP = 1;	// Start Sampling
	asm("nop");				// Sample aquisition time is determined by the number of 'nop's
	asm("nop");
	asm("nop");
	asm("nop");
	ADCON1bits.SAMP = 0;	// Start Conversion (with the pre-determined conversion clock)
	while(!ADCON1bits.DONE);
}

void ADconvSlow(void)
{
	ADCON1bits.ADON = 1;	// A/D converter module activated
	ADCON1bits.SAMP = 1;	// Start Sampling
	asm("nop");				// Sample aquisition time is determined by the number of 'nop's
	asm("nop");
	asm("nop");
	asm("nop");
	asm("nop");
	asm("nop");
	asm("nop");
	asm("nop");
	asm("nop");
	asm("nop");
	ADCON1bits.SAMP = 0;	// Start Conversion (with the pre-determined conversion clock)
	while(!ADCON1bits.DONE);
}	

//
// Hardware serial port service routines
//

//
// U1STA UART1 Status and Control Register
//
// b15 UTXISEL = 1 -> interrupt when a character is transferred to the Transmit Shift register
//					  and as result, the transmit buffer becomes empty
// b14-b12 N/A
// b11 UTXBRK = 0 U1TX pin operates normally (Transmit Break bit)
// b10 UTXEN = 1 UART transmitter enabled
// b9 UTXBF Transmit Buffer Full Status bit (read only) = 1 full, = 0 empty
// b8 TRMT Transmit Shift Register is Empty bit (read only) = 1 empty, = 0 not empty
// b7-b6 URXISEL<1:0> = 00 -> Interrupt flag bit is set when a character is received
// b5 ADDEN Address character detect (bit8 of received data = 1) 0 -> address detect disabled
// b4 RIDLE Receiver Idle bit (read only) 1 -> receiver is idle, 0 -> data is being received
// b3 PERR Parity Error Status bit 1 -> parity error, 0 -> no error (read only)
// b2 FERR Framing Error Status bit 1 -> flaming error, 0 -> no error (read only)
// b1 OERR Overrun Error Status bit 1 -> overrun error, 0 -> no error (read/clear only)
// b0 URXDA Receiver Buffer Data Available bit (read only)

//
// receivex returns 1-byte data from EIA-232C RXD pin
//
unsigned char receivex(void)
{
	while( !U1STAbits.URXDA){
		if( U1STAbits.PERR){
			return 0xaa;	// Parity Error Code
		}
		if( U1STAbits.FERR){
			return 0xbb;	// Framing Error Code
		}
		if( U1STAbits.OERR){
			U1STAbits.OERR = 0;
			return U1RXREG;	// Overrun Error Code
		}
	}
	return U1RXREG;
}

//
// transwaitx waits for the completion of EIA-232C transmission through the TXD pin
//
void transwaitx(void)
{
	while( U1STAbits.UTXBF);
}

//
// transmitx sends 1-byte data to EIA-232C TXD pin
//
void transmitx(unsigned char data)
{
	U1TXREG = data;
}

//
// Wait5mSset prepares the TMR3 timer with the time period of 5 milliseconds
//
void Wait5mSset(void)
{
	T3CON = 0x0000;	// prescaler 1:1 TMR3 STOP 16-b separated mode
	timeperiod3 = 0;
	TMR3 = 0;
	PR3 = 4607;			// 1/0.9216 * 4608 = 5000us = 5msec
	IFS0bits.T3IF = 0;
	IEC0bits.T3IE = 1;
	T3CONbits.TON = 1;	// TMR3 START
}

void Wait3mSset(void)
{
	T3CON = 0x0000;	// prescaler 1:1 TMR3 STOP 16-b separated mode
	timeperiod3 = 0;
	TMR3 = 0;
//	PR3 = 2303;			// 1/0.9216 * = 2304 = 2.5msec
	PR3 = 3225;			// 1/0.9216 * 3226 = 3.5004 msec
//	PR3 = 3501;			// 1/0.9216 * 3502 = 3.8msec
//	PR3 = 3685;			// 1/0.9216 * 3686 = 4.0msec
	IFS0bits.T3IF = 0;
	IEC0bits.T3IE = 1;
	T3CONbits.TON = 1;	// TMR3 START
}

void Wait10uS(void)
{
	asm("nop");
//	asm("nop");
// 9.77 usec
	return;
}

//
// Wait5mScheck waits for 5 milliseconds since the Wait5mSset function was invoked
//
void Wait5mScheck(void)
{
	while(!timeperiod3);
	timeperiod3 = 0;
	T3CONbits.TON = 0;	// TMR3 STOP
	IEC0bits.T3IE = 0;
}

void Wait3mScheck(void)
{
	while(!timeperiod3);
	timeperiod3 = 0;
	T3CONbits.TON = 0;	// TMR3 STOP
	IEC0bits.T3IE = 0;
}

//
// I2C master mode serial communication support routines
//

//
// WaitAck waits the ACK signal from the slave device
//
void WaitAck(void)
{
	while( I2CSTATbits.ACKSTAT);
}

//
// WaitSend waits for the termination of the data transmission from master to slave
//
void WaitSend(void)
{
	while( I2CSTATbits.TRSTAT);
}

//
// I2C 2-wire serial communication service routines
//
// I2CSTAT register
//
// b15 ACKSTAT ... 1: NACK received from slave, 0: ACK received from slave
// b14 TRSTAT ... 1: Master transmit is in progress (8-b data + ACK), 0: not in progress
// b13 - b11 N/A ... 0
// b10 BCL ... Master Bus Collision Detection bit 1: collision occurred, 0: no collision
// b9 GCSTAT ... General Call Status bit 1: General call address was received, 0: not received
// b8 ADD10 ... 10-b Address Status bit 1: 10-b address was matched, 0: not matched
// b7 IWCOL ... Write Collision Detect bit 1:an attempt to write the I2CTRN reg. failed, 0:OK
// b6 I2COV ... Receive Overflow Flag bit: 1: overflow occurred, 0: not occurred
// b5 D_A ... Data/Address bit (slave only): 1:the last byte received is data, 0: is address
// b4 P ... Stop bit 1: stop bit is detected, 0: not detected
// b3 S ... Start bit 1: start bit or repeated start bit detected, 0: not detected
// b2 R_W ... Read/Write bit information (slave only) 1: Read, 0: Write
// b1 RBF ... Receive Buffer Full Status bit: 1: Receive complete (I2CRCV is full), 0: empty
// b0 TBF ... Transmit Buffer Full Status bit: 1: Trasmit in progress (I2CTRN is full), 0: empty

//
// I2Cstart sends 'start condition' signal, then waits for the 'start' signal transmission
//
void I2Cstart(void)
{
	I2CCONbits.SEN = 1;
	while( I2CCONbits.SEN);
}

//
// I2Cstop sends 'stop condition' signal, then waits for the 'stop' signal transmission
//
void I2Cstop(void)
{
	I2CCONbits.PEN = 1;
	while( I2CCONbits.PEN);
}

//
// I2Crestart sends 'repeated start condition signal', then waits for the 'repeated start condition signal' transmission
//
void I2Crestart(void)
{
	I2CCONbits.RSEN = 1;
	while( I2CCONbits.RSEN);
}

//
// I2Cack sends 'ACK' signal to the slave
//
void I2Cack(void)
{
	I2CCONbits.ACKDT = 0;
	I2CCONbits.ACKEN = 1;
	while( I2CCONbits.ACKEN);
}

//
// I2Cnoack sends 'NOACK' signal to the slave
//
void I2Cnoack(void)
{
	I2CCONbits.ACKDT = 1;
	I2CCONbits.ACKEN = 1;
	while( I2CCONbits.ACKEN);
}

//
// I2Cread reads a 1-byte data from the slave, then returns the data (unsigned char)
// Notice: If the data has not yet come, the I2Cread function waits for the data forever
//
unsigned char I2Cread(void)
{
	unsigned char tmp;
	I2CCONbits.RCEN = 1;
	while( I2CCONbits.RCEN);
	tmp = I2CRCV;
	return tmp;
}

//
// I2Cwrite sends the data (unsigned char) to the slave device, then waits for ACK signal from the slave
//
void I2Cwrite(unsigned char data)
{
	I2CTRN = data;
	WaitSend();
	WaitAck();
}

#define I2CADDR_W 0xd0
#define I2CADDR_R 0xd1

//
// main function of the ccpx15a
//
int main(void)
{
	int i;
	unsigned char	I2Cbuf;
	unsigned char 	buf[14];
	unsigned char	data[48];
	unsigned char seq;					// sequence number
	int j;

/*
	Pin Assignment					  TRISB	   LATB

	RB0 output	Sleep_REQ				0		1
	RB1 output	LED1					0		0	-> N.A. for Party 1, Red for Party 3
	RB2	output	LED2					0		0	-> LED-A for Party 1, Blue for Party 3
	RB3 output	LED3					0		0   -> LED-B for Party 1, Green for Party 3
	RB4	output	SCL						1		1	-> must be 'input'
	RB5 input	SDA						1		1
	RB6 output	N.C.					0		0
	RB7 output	N.C.					0		0

									  TRISD	   LATD						
	RD0 input 	Push-SW					1		1

									  TRISC	   LATC
	RC13 output	Software TxD			0		1
	RC14 input  Software RxD			1		0
	RC15 N/A	OSC2 (XT mode)
*/

	TRISB = 0x0030;	// 0000 0000 0011 0000 -> RB5, RB4 for input
	LATB = 0x0031;	// 0000 0000 0011 0001 -> RB5, RB4: '1', Sleep_REQ = 1 : sleep
					// RB4 = 1 ... I2C SCL = 'H' (Idle)
					// RB5 = 1 ... I2C SDA (input port)

	TRISC = 0x4000;	// 0100 0000 0000 0000 -> RC14 for input, RC13 for output
	LATC =  0x2000;	// 0010 0000 0000 0000 -> RC13(TxD) = 1
	TRISD = 0x01;	// RD0 for input
	LATD =  0x01;	// RD0 = 1 : push switch off

	I2CADD = 0x0009;	// dummy I2C slave address (for safety)
	I2CCON = 0x9000;	// I2C enabled
						// b15 I2CEN  ... = 1 I2C module enabled
						// b14 N/A ... 0
						// b13 I2CSIDL ... 0 Idling mode disabled
						// b12 SCLREL ... 1
						// b11 IPMIEN ... 0
						// b10 A10M ... 0 10-b address mode disabled
						// b9 DISSLW ... 0 slew rate control enabled
						// b8 SMEN ... 0 SMBus input level specification not referred
						// b7 GCEN ... 0 General Call disabled
						// b6 STREN ... 0 disable receive clock stretching (slave only)
						// b5 ACKDT ... 0 ACK mode(should be changed in the communication)
						//			(... 1 NACK mode)
						// b4 ACKEN ... 0 ACK sequence not in progress ( ... 1 sends ACK)
						// b3 RCEN ... 0 Receive sequence not in progress ( ... 1 receive start)
						// b2 PEN .. 0 Stop condition not in progress ( ... 1 stop condition goes)
						// b1 RSEN ... 0 Repeated start condiiton not in progress
						// b0 SEN ... 0 Start condition not in progress
	I2CBRG = 1;			// 326KHz SCL @ fclk = 3.6864MHz, 345KHz @ fclk = 4.000MHz
						// I2CBRG = (Fcy/Fscl - Fcy/1,111,111) - 1
						// Fcy = 0.9216MHz, I2CBRG = (921.6/326 - 921.6/1111.111) - 1 = 1

	ADCON1 = 0;
					// b15 ADON = 1: A/D converter module is operating, 0: off
					// b14 N/A
					// b13 ADSIDL = 0: A/D converter idling mode is disabled
					// b12-b10 N/A
					// b9-8 FORM<1:0> 00 ... data = 0000 dddd dddd dddd (right-justified)
					// b7-5 SSRC<2:0> 000 ... clearing SAMP bit ends sampling and starts conversion
					// b4-3 N/A
					// b2 ASAM 0 ... Sampling begins when SAMP bit set (not automatic scan mode)
					// b1 SAMP 0 ... A/D sample/hold amplifiers are holding -> 1 ... start sampling
					// b0 DONE A/D Conversion Status bit 1 ... A/D conversion is done, 0 ... not yet
	ADCON2 = 0;
					// b15-b13 VCFG<2:0> Voltage Reference Configuration bits = 000 (AVdd-AVss)
					// b12 0 (reserved)
					// b11 N/A
					// b10 CSCNA Scan Input Selections ... 0:do not scan inputs, 1 ... scan inputs
					// b9-b8 N/A
					// b7 BUFS Buffer Fill Status bit (not used in this program)
					// b6 N/A
					// b5-b2 SMPI<3:0> Sample/Convert Sequences Per Interrupt Selection bits = 0
					//      0 ... each sample/conver sequence generates intterrupt
					// b1 BUFM = 0 Buffer configured as one 16-word buffer (default)
					// b0 ALTS = 0 Always use MUX A input multiplexer settings (default)
	ADCON3 = 0x0001;
					// b15-b13 N/A
					// b12-b8 SAMC<4:0> Auto Sample Time bits = 00000 -> 0 Tad (default)
					// b7 ADRC A/D Conversion Clock Source bit = 0 -> clock derived from Tclk
					// b6 N/A
					// b5-b0 ADCS<5:0> A/D Conversion Clock Select bits = 000000
					//    Tcy/2 * (ADCS<5:0> + 1) = Tcy/2 = 0.5usec

	ADPCFG = 0x00ff;	// 0000 0000 1111 1111 -> RB0, RB1, RB2, RB3, RB4, RB5, RB6, RB7 for digital input
	ADCSSL = 0x0000;	// 0000 0000 0000 0000 -> no scanning

	LATBbits.LATB2 = 1;	// light the LED on RB2
	Wait1S();
	LATBbits.LATB2 = 0;	// off the LED
	Wait1S();

	while( PORTDbits.RD0 == 1);

	LATBbits.LATB1 = 1;	// light the LED on RB1
	Wait1S();
	LATBbits.LATB1 = 0;	// off the LED
	Wait1S();

	LATBbits.LATB2 = 1;	// light the LED on RB2
	Wait1S();
	LATBbits.LATB2 = 0;	// off the LED
	Wait1S();

	LATBbits.LATB3 = 1;	// light the LED on RB3
	Wait1S();
	LATBbits.LATB3 = 0;	// off the LED
	Wait1S();

	while( PORTDbits.RD0 == 1);

	Wait1S();
	I2Cbuf = 0x00;
	I2Cstart();
	I2Cwrite(I2CADDR_W);
	I2Cwrite(0x75);			// Who am I command
	I2Crestart();
	I2Cwrite(I2CADDR_R);
	I2Cbuf = I2Cread();
	I2Cnoack();
	I2Cstop();	
	if(I2Cbuf != 0x68){		// MPU-6050 Not Found ...
		LATBbits.LATB2 = 1;	// light the LED on RB2
		Wait05S();
		LATBbits.LATB2 = 0;	// off the LED
		Wait05S();		
	}

	I2Cstart();
	I2Cwrite(I2CADDR_W);
	I2Cwrite(0x19);		// Sample Rate Divider
	I2Cwrite(9);		// 1KHz/(9+1) = 100Hz
	I2Cstop();

	I2Cstart();
	I2Cwrite(I2CADDR_W);
	I2Cwrite(0x1a);		// Configuration
	I2Cwrite(0x03);		// DLPF_CFG = 3 (BW = 44/42Hz for acc/gyro)
	I2Cstop();

	I2Cstart();
	I2Cwrite(I2CADDR_W);
	I2Cwrite(0x6b);		// Power Management #1
	I2Cwrite(0x01);		// SLEEP = 0 (sleep disabled), CLKSEL = 1 (gyro-x PLL)
	I2Cstop();

	Wait1S();			// for stability

	LATBbits.LATB2 = 1;	// light the LED on RB2
	Wait1S();
	LATBbits.LATB2 = 0;	// off the LED
	Wait1S();

	while( PORTDbits.RD0 == 1);

#if 0
						// Hardware EIA-232C communication
	U1BRG = 0;			// Fcy = 3.6864MHz/4 = 0.9216MHz
						// U1BRG = Fcy/(16xBaud Rate) - 1
						// U1BRG = 0
						// Baud Rate = Fcy/(16*(U1BRG + 1)) = 57,600bps
						// 
	U1MODE = 0x8400;	// 1x0xx1xx 000xx000
						// b15 UARTEN = 1 -> UART1 module enabled
						// b14 N/A
						// b13 USIDL = 0 -> UART1 idling mode disabled
						// b12 N/A
						// b11 reserved (0)
						// b10 ALTIO = 1 -> U1ATX/U1ARX are used instead of U1TX/U1RX
						// b9-b8 reserved (00)
						// b7 WAKE = 0 -> Wake-up disabled (default)
						// b6 LPBACK = 0 -> Loopback mode is disabled
						// b5 ABAUD = 0 (Auto Baud Enable bit) input to capture module from ICx pin
						// b4-b3 N/A
						// b2-b1 PDSEL<1:0> Parity and Data Selection bits = 00 8-b data, no parity
						// b0 STSEL Stop Selection bit = 0 -> 1 Stop bit (1 ... 2 Stop bits)
	U1STAbits.UTXEN = 1;
	U1STAbits.UTXISEL = 1;
	U1STAbits.URXISEL = 0;	// a single received character makes INT
							// NOTICE URXISEL consists of 2-bit <URXISEL1:URXISEL2>
//	U1STAbits.URXISEL = 3;	// receiving 4 words makes INT

	U1STAbits.OERR = 0;		// overrun error flag cleared
	U1STAbits.PERR = 0;		// parity error flag cleared
	U1STAbits.FERR = 0;		// framing error flag cleared

	U1STAbits.URXDA = 0;	// buffer flush

	IEC0bits.U1TXIE	= 0;	// EIA-232C TXD interrupt is disabled
	IFS0bits.U1RXIF = 0;
	IEC0bits.U1RXIE = 1;	// EIA-232C RXD interrupt is enabled
#endif

	// TMR1 settings ... TMR1 for the main 10msec sampling interval timer
	T1CON = 0;
						// b16 TON	Time ON Control bit = 1 -> starts the time, 0 -> stops the timer
						// b14 N/A
						// b13 TSIDL Stop in Idle Mode bit = 0 (disabled)
						// b12-b7 N/A
						// b6 TGATE Timer Gated Time Accumulation Enable bit : 0 -> disabled
						// b5-b4 TCKPS<1:0> Time Input Clock Prescale Select bits
						//   00 ... 1:1		01 ... 1:8		10 ... 1:64		11 ... 1:256
						// b3 N/A (TMR2 : T32 32-b Timer Mode Select bit = 0 : TMR2 and TMR3 = 16-b
						// b2 TSYNC = 0 -> do not synchronize external clock input
						// b1 TCS Timer Clock Source Select bit = 0 -> internal clock (Fosc/4 = Fcy)
						// b0 N/A
	TMR1 = 0;
	PR1 = (1152 - 1);	// 1/0.9216 us x 1152 x 8 = 10,000 us = 10msec (100Hz sampling)
	IFS0bits.T1IF = 0;
	timeperiod = 0;

	LATBbits.LATB2 = 1; // light the LED on RB2

	T1CON = 0x0010;	// 0000 0000 0001 0000 prescaler 1:8 TMR1 STOP
	IEC0bits.T1IE = 1;

	seq = 0;	// frame sequence number

	T1CONbits.TON = 1;	// TMR1 START
	j = 0;

	while(1){
		while(!timeperiod);
		timeperiod = 0;
		if(j == 36){
			LATBbits.LATB0 = 0;	// RF module wakes up
			Wait5mSset();
		}

		I2Cstart();
		I2Cwrite(I2CADDR_W);
		I2Cwrite(0x3b);		// Ax_H address
		I2Crestart();
		I2Cwrite(I2CADDR_R);

		for( i = 0; i < 13; i++){
			buf[i] = I2Cread();
			I2Cack();
		}
		buf[i] = I2Cread();
		I2Cnoack();
		I2Cstop();

		for( i = 0; i < 6; i++){
			data[j] = buf[i];
			j++;
		}
		for( i = 8; i < 14; i++){
			data[j] = buf[i];
			j++;
		} 

		if(j == 48){
			Wait5mScheck();

			transmit(seq);
			for( i = 0; i < 48; i++){
				transmit(data[i]);
			}
			LATBbits.LATB0 = 1; // RF module sleeps
			seq++;
			j = 0;
		}
#if 0
		transmit(data[0]);	// AxH
		transmit(data[1]);	// AxL
		transmit(data[2]);	// AyH
		transmit(data[3]);	// AyL
		transmit(data[4]);	// AzH
		transmit(data[5]);	// AzL
		transmit(data[8]);	// GxH
		transmit(data[9]);	// GxL
		transmit(data[10]);	// GyH
		transmit(data[11]);	// GyL
		transmit(data[12]);	// GzH
		transmit(data[13]);	// GzL
		transmit(data[6]);	// TempH
		transmit(data[7]);	// TempL
#endif

	}
}
