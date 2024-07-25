package miniRV_single_cycle.OnBoard

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage

class SmdLedIO extends Bundle {
    val we = Input(Bool())
    val addr = Input(UInt(ADDR_WIDTH.W))
    val wD = Input(UInt(DATA_WIDTH.W))

    val led = Output(UInt(LED_NUMBER.W)) // through top to led
}
class SmdLed extends Module {
    val io = IO(new SmdLedIO)
    val led_r = RegInit(0.U(LED_NUMBER.W)) // use reg 
    when(io.we && io.addr === PERI_ADDR_LED)
    {
        led_r := io.wD
    }
    io.led := led_r
}

object mySmdLed extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new SmdLed,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}