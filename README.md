# cs3502 operating systems project

## cpu emulator

The word size is 32 bits. Memory is byte-addressed and aligned to the word boundary.

There are 16 registers holding one word each. Register 0 is the accumulator, and
register 1 is the zero register. Neither have any special function in the
instruction set, and they may be used as normal registers.

### instruction set

| opcode | type | fake opcode | name | does |
| --- | ---  | --- | --- | --- |
| `c0` | io | `00` | rd   | if r2 != 0, r1 = *r2; else r1 = *addr  |
| `c1` | io | `01` | wr   | if r2 != 0, *r2 = r1; else *addr = r1 |
| `42` | i  | `02` | st   | *d = b |
| `43` | i  | `03` | lw   | d = *(addr + b) |
| `04` | r  | `04` | mov  | s1 = s2 |
| `05` | r  | `05` | add  | d = s1 + s2 |
| `06` | r  | `06` | sub  | d = s1 - s2 |
| `07` | r  | `07` | mul  | d = s1 * s2 |
| `08` | r  | `08` | div  | d = s1 / s2 |
| `09` | r  | `09` | and  | d = s1 AND s2 |
| `0a` | r  | `0a` | or   | d = s1 OR s2 |
| `4b` | i  | `0b` | movi | d = addr |
| `4c` | i  | `0c` | addi | d += addr |
| `4d` | i  | `0d` | muli | d *= addr |
| `4e` | i  | `0e` | divi | d /= addr |
| `4f` | i  | `0f` | ldi  | d = addr (same as movi? idk) |
| `10` | r  | `10` | slt  | d = (s1 < s2) |
| `51` | i  | `11` | slti | d = (b < addr) |
| `92` | j  | `12` | hlt  | halts cpu |
| ? | ?  | `13` | nop  | nothing |
| `94` | j  | `14` | jmp  | ip = addr |
| `55` | i  | `15` | beq  | if b == d, ip = addr |
| `56` | i  | `16` | bne  | if b != d, ip = addr |
| `57` | i  | `17` | bez  | if b == 0, ip = addr |
| `58` | i  | `18` | bnz  | if b != 0, ip = addr |
| `59` | i  | `19` | bgz  | if b > 0, ip = addr |
| `5a` | i  | `1a` | blz  | if b < 0, ip = addr |

note that for types r and i, register d is at a different bit location within
the instruction. see below.

### type r (arithmetic)

|     |     |     |     |     |     |
| --- | --- | --- | --- | --- | --- |
| 2 | 6 | 4 | 4 | 4 | 12 |
| `00` | fake opcode | s1 | s2 | d | `000000000000` |

### type i (conditional/immediate)

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| 2 | 6 | 4 | 4 | 16 |
| `01` | fake opcode | b | d | addr |

### type j (halt/unconditional jump)

|     |     |     |
| --- | --- | --- |
| 2 | 6 | 24 |
| `10` | fake opcode | addr |

### type io (rd/wr)

These instructions are preempting.

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| 2 | 6 | 4 | 4 | 16 |
| `11` | fake opcode | r1 | r2 | addr |
