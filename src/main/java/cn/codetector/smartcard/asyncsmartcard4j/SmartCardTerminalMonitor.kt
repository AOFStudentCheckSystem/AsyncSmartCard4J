package cn.codetector.smartcard.asyncsmartcard4j

import java.util.*
import javax.smartcardio.CardTerminal
import javax.smartcardio.TerminalFactory

class SmartCardTerminalMonitor constructor(val factory: TerminalFactory): Runnable{
    private val listenerList:MutableList<TerminalEventListener> = ArrayList<TerminalEventListener>();
    private val cardTerminals:MutableList<CardTerminal> = ArrayList<CardTerminal>()

    private fun compareTerminals(newTerminals: List<CardTerminal>): Boolean{
        var returnValue = true;
        if (newTerminals.size != cardTerminals.size){
            for (i in newTerminals.indices){
                if (newTerminals[i] != cardTerminals[i]){
                    returnValue = false;
                    break;
                }
            }
        }
        if(!returnValue) {
            //Update List
            cardTerminals.clear()
            newTerminals.forEach (fun(terminal:CardTerminal){
                cardTerminals.add(terminal)
            })
        }

        return returnValue;
    }

    override fun run() {
        if (compareTerminals(factory.terminals().list())){
            val iterator = listenerList.iterator();
            while (iterator.hasNext()){
                try {
                    iterator.next().onChange(cardTerminals);
                } catch (e : Throwable){
                    iterator.remove()
                    System.err.println("An un-removed null Terminal listener found");
                }
            }
        }
    }
    protected interface TerminalEventListener{
        fun onChange(terminals : List<CardTerminal>)
    }
}