package miniRV_single_cycle

import chisel3._
import chisel3.util._
import utils.DRAM
import config.Configs._
import _root_.circt.stage.ChiselStage
import utils.DataMemControl
import utils.LS_TYPES._
class DataMemIO extends Bundle {
  val ctl = new DataMemControl()
  val addr = Input(UInt(ADDR_WIDTH.W))
  val dataStore = Input(UInt(DATA_WIDTH.W))
  val dataLoad = Output(UInt(DATA_WIDTH.W))
}

class DataMem extends Module {
  val io = IO(new DataMemIO())

  val dram = Module(new DRAM)
  dram.io.clk := clock
  dram.io.a := io.addr >> DATA_BYTE_WIDTH_LOG.U
  val dataLoadBeforeProcess = WireDefault(0.U(DATA_WIDTH.W))

  dataLoadBeforeProcess := dram.io.spo // load from mem

  val dataLoad = WireDefault(0.U(DATA_WIDTH.W))
  val d = WireDefault(0.U(DATA_WIDTH.W))
  val we = WireDefault(false.B)

  // Store
  when(io.ctl.isStore) {
    we := true.B
    when(io.ctl.ctrlLSType === LS_W) { // 修改全部4字节
      d := io.dataStore
    }.elsewhen(io.ctl.ctrlLSType === LS_H) { // 修改低2字节
      d := Cat(dataLoadBeforeProcess(31, 16), io.dataStore(15, 0))
    }.otherwise { // 修改最低一个字节
      d := Cat(dataLoadBeforeProcess(31, 8), io.dataStore(7, 0))
    }
  }

  // Load
  when(io.ctl.isLoad) {
    when(io.ctl.ctrlLSType === LS_W) {
      dataLoad := dataLoadBeforeProcess // word
    }.elsewhen(io.ctl.ctrlLSType === LS_H) {
      when(io.ctl.isSigned) {
        dataLoad := Cat(Fill(ADDR_WIDTH - 16, dataLoadBeforeProcess(15)), dataLoadBeforeProcess(15, 0)) // half word
      }.otherwise {
        dataLoad := Cat(Fill(ADDR_WIDTH - 16, 0.U), dataLoadBeforeProcess(15, 0))
      }
    }.otherwise {
      when(io.ctl.isSigned) {
        dataLoad := Cat(Fill(ADDR_WIDTH - 8, dataLoadBeforeProcess(7)), dataLoadBeforeProcess(7, 0)) // byte
      }.otherwise {
        dataLoad := Cat(Fill(ADDR_WIDTH - 8, 0.U), dataLoadBeforeProcess(7, 0))
      }
    }
  }
  io.dataLoad := dataLoad
  dram.io.we := we
  dram.io.d := d
}

object myDataMem extends App {
    println(
    ChiselStage.emitSystemVerilog(
      new DataMem,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
