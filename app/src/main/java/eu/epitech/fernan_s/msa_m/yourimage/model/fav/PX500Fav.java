package eu.epitech.fernan_s.msa_m.yourimage.model.fav;


import com.orm.SugarRecord;

import eu.epitech.fernan_s.msa_m.yourimage.model.thread.PX500Thread;

public class PX500Fav extends SugarRecord{
        PX500Thread _thread;

        public PX500Fav() {}

        public PX500Fav(PX500Thread thread) {
            _thread = thread;
            long threadid = Long.parseLong(_thread.getID(), 10);
            _thread.setId(threadid);
            SugarRecord.save(_thread);
            this.setId(threadid);
        }

        public PX500Thread getThread() {
            return SugarRecord.findById(PX500Thread.class, getId());
        }
}
