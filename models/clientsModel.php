<?php
require 'models/client.php';
class clientsModel extends Model{
    function __construct(){
        parent::__construct();
    }
    function getAll(){
        $items=[];
        try{
            $query=$this->db->connect()->query('SELECT * FROM client');

            while($row=$query->fetch()){
                $item=new Client();
                $item->code=$row['c_code'];
                $item->name=$row['c_name'];
                $item->lastName=$row['last_name'];
                $item->phone=$row['phone'];
                $item->age=$row['age'];
                array_push($items, $item);
            }
            return $items;
        }catch (PDOException $e){
            return [];
        }
    }
    function getClientsByPhysicals($clientsID){
        $clientItems=[];
        try{
            foreach($clientsID as $rowPhysical){
                $query=$this->db->connect()->prepare('SELECT * FROM client WHERE c_code=:code');
                $query->execute(['code'=>$rowPhysical->code]);
                while($row=$query->fetch()){
                    $item=new Client();
                    $item->code=$row['c_code'];
                    $item->name=$row['c_name'];
                    $item->lastName=$row['last_name'];
                    $item->phone=$row['phone'];
                    $item->age=$row['age'];
                    array_push($clientItems, $item);
                }
            }
            return $clientItems;
        }catch (PDOException $e){
            return [];
        }
    }
    function getAllPhysical(){
        $items=[];
        try{
            $query=$this->db->connect()->query('SELECT * FROM clientphysical');

            while($row=$query->fetch()){
                $item=new Client();
                $item->code=$row['cp_code'];
                array_push($items, $item);
            }
            return $items;
        }catch (PDOException $e){
            return [];
        }
    }
    function registerPhysical($data){
        try{
            $query=$this->db->connect()->prepare('INSERT INTO client(c_code, c_name, last_name, phone, age) 
                                                  VALUES(:id, :name, :last_name, :phone, :age)');
            $query->execute(['id'=>$data['id'], 'name'=>$data['name'], 'last_name'=>$data['last_name'], 'phone'=>$data['phone'], 'age'=>$data['age']]);

            $query=$this->db->connect()->prepare('INSERT INTO clientphysical(cp_code) 
                                                  VALUES(:id)');
            $query->execute(['id'=>$data['id']]);
            return true;
        }catch (PDOException $e){
            return false;
        }
    }
    function isIDDuplicate($id){
        try{
            $query=$this->db->connect()->prepare('SELECT * FROM client WHERE c_code=:code');
            $query->execute(['code'=>$id]);

            return $query->rowCount()>0;
        }catch (PDOException $e){
            return false;
        }
    }
}
?>