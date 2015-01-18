;
;	assembly language programm for software EIA-232C support
;	115200 bps (8 machine clock per bit)
;
	.equ __30F3012, 1
	.include "p30f3012.inc"
;
; transmit(character);
; unsigned char character; -> w0 register
;
	.text
	.global _transmit
	.global _receivedetect
_transmit:

	bclr	LATC, #13	; start bit

	push	W0	; 1
	push	W1	; 2

	mov		#8, W1	; 3
	nop		; 4
_t_loop:
	btsc	W0, #0	; 5, 6
	goto	_t_bit1	; 6 & 7
_t_bit0:
	bclr	LATC, #13	; 1

	lsr		W0, W0		; 2
	dec		W1, W1		; 3
	bra		NZ, _t_loop	; 4, 5
	goto	_t_last		; 5, 6
_t_bit1:
	bset	LATC, #13	; 1

	lsr		W0, W0		; 2
	dec		W1, W1		; 3
	bra		NZ, _t_loop	; 4, 5
	nop		; 5
	nop		; 6
_t_last:
	nop		; 7
	nop		; 8
	bset	LATC, #13	; stop bit ; 1

	nop		; 2
	nop		; 3
	nop		; 4

	pop		W1	; 5
	pop		W0	; 6
	return	; 7, 8

_receivedetect:
	btsc	PORTC, #14	; RXD pin
	goto	_receivedetect
	return

	.end

	
	