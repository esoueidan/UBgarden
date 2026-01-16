package fr.ubx.poo.ugarden.go.decor;

import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.bonus.Bonus;
import fr.ubx.poo.ugarden.go.decor.Decor;
import fr.ubx.poo.ugarden.go.personage.Player;

public class Door extends Decor {

    private boolean open = false;

    private boolean updated = false;

    public Door(Position position) {
        super(position);
    }

    public boolean isOpen(){
        if (open) return true;
        return false;
    }

    public boolean isUpdated(){
        if (updated) return true;
        return false;
    }

    public void setDoorState(boolean state){
        this.open = state;
    }

    public void setUpdatedState(boolean state){
        this.updated = state;
    }

    public String getState(){
        if(open) return "OPENED";
        return "CLOSED";
    }

    @Override
    public boolean walkableBy(Player player) {
        if(player.getKey() == 0 && !this.isOpen())
            return false;
        return true;
    }

    @Override
    public void takenBy(Player player) {player.take(this);}
}
