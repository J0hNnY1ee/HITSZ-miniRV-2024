package miniRV_single_cycle.OnBoard

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage

class SwIO extends Bundle { // only Read
  
  val addr = Input(UInt(ADDR_WIDTH.W))
  val sw = Input(UInt(SWITCH_NUMBER.W))

  val swData = Output(UInt(DATA_WIDTH.W))
}

class Switch extends Module {

  val io = IO(new SwIO)
  val swData = WireDefault(0.U(32.W))
  when(io.addr === PERI_ADDR_SW) {
    swData := Cat(Fill(8, 0.U), io.sw)
  }
  io.swData := swData
}

object mySwitch extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new Switch,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
