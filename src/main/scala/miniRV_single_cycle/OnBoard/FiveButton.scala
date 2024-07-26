package miniRV_single_cycle.OnBoard

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage

class FiveButtonIO extends Bundle {
  val addr = Input(UInt(ADDR_WIDTH.W))
  val button = Input(UInt(BUTTON_NUMBER.W))

  val btData = Output(UInt(DATA_WIDTH.W))
}

class FiveButton extends Module {
  val io = IO(new FiveButtonIO())
  val btData = WireDefault(0.U(32.W))
  when(io.addr === PERI_ADDR_BTN) {
    btData := Cat(Fill(27, 0.U), io.button)
  }
  io.btData := btData
}

object myFiveButton extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new  FiveButton,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}