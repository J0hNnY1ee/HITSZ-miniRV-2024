package config

import chisel3._

import math._

object Configs {
  val ADDR_WIDTH = 32 // 地址位宽
  val ADDR_BYTE_WIDTH = ADDR_WIDTH / 8 // 地址位宽按字节算
  val DATA_WIDTH = 32 // 数据位宽
  val DATA_WIDTH_H = 16 // 半字数据位宽
  val DATA_WIDTH_B = 8 // 字节数据位宽

  val INST_WIDTH = 32 // 指令位宽
  val INST_BYTE_WIDTH = INST_WIDTH / 8 // 指令位宽按字节算
  val INST_BYTE_WIDTH_LOG =
    ceil(log(INST_BYTE_WIDTH) / log(2)).toInt // 指令地址对齐的偏移量
  val MEM_INST_SIZE = 1024 // 指令内存大小

  val DATA_BYTE_WIDTH = DATA_WIDTH / 8 // 数据位宽按字节算
  val DATA_BYTE_WIDTH_LOG =
    ceil(log(DATA_BYTE_WIDTH) / log(2)).toInt // 数据地址对齐的偏移量
  val MEM_DATA_SIZE = 1024 // 数据内存大小

  val START_ADDR: Long = 0x00000000 // 起始执行地址

  val REG_NUMS = 32 // 寄存器数量
  val REG_NUMS_LOG = ceil(log(REG_NUMS) / log(2)).toInt // 寄存器号位数

  val SWITCH_NUMBER = 24
  val LED_NUMBER = 24
  val DIG_LED_NUMBER = 8
  val BUTTON_NUMBER = 5

  val Trace_test = 1.B
  val PERI_ADDR_DIG = "hFFFF_F000".U
  val PERI_ADDR_LED = "hFFFF_F060".U
  val PERI_ADDR_SW = "hFFFF_F070".U
  val PERI_ADDR_BTN = "hFFFF_F078".U
}

object test extends App {}
