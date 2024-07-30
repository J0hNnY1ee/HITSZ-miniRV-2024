HITSZ-miniRV-2024
================
## OverView
### Project Introduction
This is the course assignment for 'Computer Design and Implementation' at Harbin Institute of Technology, Shenzhen, in 2024. It completing a five-stage pipeline miniRisc-V CPU.

The list of instructions  are part of the RISC-V instruction set architecture (ISA) , contains `addi` , `sltu` , `sra` , `andi` , `sb` , `sw` , `bltu` , `blt` , `sub` , `slt` , `sll` , `srai` , `slli` , `simple` , `auipc` , `lb` , `and` , `bne` , `add` , `lhu` , `xor` , `bge` , `lbu` , `ori` , `jalr` , `lw` , `beq` , `jal` , `sltiu` , `or` , `slti` , `bgeu` , `lui` , `sh` , `srl` , `xori` , `lh` and `srli`.
## Relevant resourse
- `Guidance book` : https://comp2012.pages.dev/
- `template` : https://github.com/chipsalliance/chisel-template

## Usage
### Deploy environment
Please refer to the following sources deploy environment.
-  https://www.chisel-lang.org/docs
- https://blog.csdn.net/weixin_43681766/article/details/124877558

### Compile
Execute following commond
```shell
mill -i miniRV
``` 
then you will get a systemVirilog file `Top_Onboard.sv`