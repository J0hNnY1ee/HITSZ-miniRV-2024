package miniRV_single_cycle

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage
import utils.DigLed_DN
import java.security.Signer
import upickle.default

class Bridge extends Module {}
class button extends Module {
  val io = IO(new Bundle {
    val addr = Input(UInt(12.W))
    val button_input = Input(UInt(5.W))
    val dataOut = Output(UInt(DATA_WIDTH.W))
  })
  val dataOut = RegInit(0.U(DATA_WIDTH.W))
  dataOut := Cat(Fill(27, 0.U), io.button_input)
  io.dataOut := dataOut
}
object myButton extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new button,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}

class led extends Module {
  val io = IO(new Bundle {
    val addr = Input(UInt(24.W))
    val we = Input(Bool())
    val wD = Input(UInt(32.W))
    val led = Output(UInt(24.W))
  })
  val led = RegInit(0.U(24.W))
  when(io.we && io.addr === 0x60.U) {
    led := io.wD
  }
  io.led := led
}
object myled extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new led,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}


class Digital_LEDs extends Module {
  val io = IO(new Bundle {
    val addr = Input(UInt(ADDR_WIDTH.W))
    val we = Input(Bool())
    val wD = Input(UInt(DATA_WIDTH.W))
    val dN = new DigLed_DN()
    val dig_en = Output(UInt(8.W))

  })
  val r = WireDefault(VecInit(Seq.fill(8)(0.U(8.W))))
  val wD = RegInit(0.U(32.W))
  val dN = WireDefault("b1111_1111".U(8.W))
  when(io.we && io.addr === 0.U) {
    wD := io.wD
  }
  val display_0 = Module(new Digital_LEDs_Display())
  val display_1 = Module(new Digital_LEDs_Display())
  val display_2 = Module(new Digital_LEDs_Display())
  val display_3 = Module(new Digital_LEDs_Display())
  val display_4 = Module(new Digital_LEDs_Display())
  val display_5 = Module(new Digital_LEDs_Display())
  val display_6 = Module(new Digital_LEDs_Display())
  val display_7 = Module(new Digital_LEDs_Display())

  display_0.io.Num := wD(3, 0)
  display_1.io.Num := wD(7, 4)
  display_2.io.Num := wD(11, 8)
  display_3.io.Num := wD(15, 12)
  display_4.io.Num := wD(19, 16)
  display_5.io.Num := wD(23, 20)
  display_6.io.Num := wD(27, 24)
  display_7.io.Num := wD(31, 28)

  r(0) := display_0.io.Signal
  r(1) := display_1.io.Signal
  r(2) := display_2.io.Signal
  r(3) := display_3.io.Signal
  r(4) := display_4.io.Signal
  r(5) := display_5.io.Signal
  r(6) := display_6.io.Signal
  r(7) := display_7.io.Signal

  switch(io.dig_en) {
    is("b1111_1110".U) { dN := r(0) }
    is("b1111_1101".U) { dN := r(1) }
    is("b1111_1011".U) { dN := r(2) }
    is("b1111_0111".U) { dN := r(3) }
    is("b1110_1111".U) { dN := r(4) }
    is("b1101_1111".U) { dN := r(5) }
    is("b1011_1111".U) { dN := r(6) }
    is("b0111_1111".U) { dN := r(7) }
  }
  val timer = Module(new timerForLed(29999.U))
  timer.io.we := io.we
  io.dig_en := timer.io.led_en
  io.dN.DN_A := dN(7)
  io.dN.DN_B := dN(6)
  io.dN.DN_C := dN(5)
  io.dN.DN_D := dN(4)
  io.dN.DN_E := dN(3)
  io.dN.DN_F := dN(2)
  io.dN.DN_G := dN(1)
  io.dN.DN_DP := dN(0)

}

object myDigital_LEDs extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new Digital_LEDs,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
class Digital_LEDs_Display extends Module {
  val io = IO(new Bundle {
    val Num = Input(UInt(3.W))
    val Signal = Output(UInt(8.W))
  })
  val Signal = WireDefault("b11111111".U(8.W))
  switch(io.Num) {
    is(0.U) { Signal := "b00000011".U }
    is(1.U) { Signal := "b10011111".U }
    is(2.U) { Signal := "b00100101".U }
    is(3.U) { Signal := "b00001101".U }
    is(4.U) { Signal := "b10011001".U }
    is(5.U) { Signal := "b01001001".U }
    is(6.U) { Signal := "b01000001".U }
    is(7.U) { Signal := "b00011111".U }
    is(8.U) { Signal := "b00000001".U }
    is(9.U) { Signal := "b00011001".U }
    is(10.U) { Signal := "b00010001".U }
    is(11.U) { Signal := "b11000001".U }
    is(12.U) { Signal := "b11100101".U }
    is(13.U) { Signal := "b10000101".U }
    is(14.U) { Signal := "b01100001".U }
    is(15.U) { Signal := "b01110001".U }
  }
  io.Signal := Signal
}
object myDigital_LEDs_Display extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new Digital_LEDs_Display,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}

class timerForLed(time_set: UInt) extends Module {
  val io = IO(new Bundle {
    val we = Input(Bool())
    val led_en = Output(UInt(8.W))
  })
  val clock_inc = RegInit(0.B)
  val clk_accu = RegInit(0.U(25.W))
  val clock_end = clock_inc & (clk_accu === time_set)
  val led_en = RegInit("b11111110".U(8.W))
  when(io.we) {
    clock_inc := 1.B
  }
  when(clock_end) {
    clk_accu := 0.U
  }.elsewhen(clock_inc) {
    clk_accu := clk_accu + 1.U
  }
  when(clock_end) {
    led_en := Cat(led_en(6, 0), led_en(7))
  }

  io.led_en := led_en
}
object mytimer extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new timerForLed(29999.U),
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
